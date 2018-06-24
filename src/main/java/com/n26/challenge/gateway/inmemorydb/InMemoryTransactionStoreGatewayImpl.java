package com.n26.challenge.gateway.inmemorydb;

import com.n26.challenge.domain.TransactionMetric;
import com.n26.challenge.domain.TransactionStatistics;
import com.n26.challenge.gateway.TransactionStoreGateway;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Component
public class InMemoryTransactionStoreGatewayImpl implements TransactionStoreGateway {

    private final List<TransactionMetric> inMemoryDatabase;
    private final ReentrantLock reentrantLock;
    private final TransactionStatistics transactionStatistics;

    public InMemoryTransactionStoreGatewayImpl() {
        this.inMemoryDatabase = new ArrayList<>();
        this.reentrantLock = new ReentrantLock();
        this.transactionStatistics = new TransactionStatistics();
    }

    @Override
    public void saveNewMetric(final TransactionMetric newTransactionMetric) {
        this.reentrantLock.lock();

        try {
            this.inMemoryDatabase.add(newTransactionMetric);
            calculateAndUpdateMin(newTransactionMetric.getAmount());
            calculateAndUpdateMax(newTransactionMetric.getAmount());
            calculateAndUpdateSum(newTransactionMetric.getAmount());
            calculateAndUpdateCount();
            calculateAndUpdateAVG();
        } finally {
            this.reentrantLock.unlock();
        }
    }

    /**
     * Calculates all the metric for the given Metrics Collected
     * If there are no more metrics, the statistics will be updated to zero
     */
    private void calculateAndUpdateMetrics() {
        this.inMemoryDatabase
                .forEach(transactionMetric -> {
                    calculateAndUpdateMin(transactionMetric.getAmount());
                    calculateAndUpdateMax(transactionMetric.getAmount());
                    calculateAndUpdateSum(transactionMetric.getAmount());
                });

        calculateAndUpdateCount();
        calculateAndUpdateAVG();
    }

    private void clearAllStatistic() {
        this.transactionStatistics.setCount(0L);
        this.transactionStatistics.setSum(0D);
        this.transactionStatistics.setMin(Double.MAX_VALUE);
        this.transactionStatistics.setMax(Double.MIN_VALUE);
        this.transactionStatistics.setAvg(0D);
    }

    private void calculateAndUpdateCount() {
        this.transactionStatistics.setCount((long) this.inMemoryDatabase.size());
    }

    @Override
    public void removeOldMetrics(final Integer maxTransactionsTimeOfLifeInSeconds) {
        this.reentrantLock.lock();
        try {
            List<TransactionMetric> inMemoryDatabaseWithOldTransactionMetrics = this.inMemoryDatabase.parallelStream()
                    .filter(transactionMetric -> isTransactionMetricOlderThan(transactionMetric.getTime(), maxTransactionsTimeOfLifeInSeconds))
                    .collect(Collectors.toList());

            inMemoryDatabase.removeAll(inMemoryDatabaseWithOldTransactionMetrics);

            clearAllStatistic();
            calculateAndUpdateMetrics();

        } finally {
            this.reentrantLock.unlock();
        }
    }

    /**
     * @return Returns a copy of the current object used to store the statistics
     * No operation is performed in the TransactionMetrics collected so far.
     * So, O(1)
     */
    @Override
    public Optional<TransactionStatistics> getTransactionMetrics() {
        this.reentrantLock.lock();
        try {
            if (this.inMemoryDatabase.isEmpty()) {
                return Optional.empty();
            }
            return Optional.ofNullable(this.transactionStatistics.copy());
        } finally {
            this.reentrantLock.unlock();
        }

    }

    private boolean isTransactionMetricOlderThan(final LocalDateTime transactionMetric, final Integer maxTimeOfLiveInSeconds) {
        long differenceIsSeconds = transactionMetric.until(LocalDateTime.now(ZoneOffset.UTC), ChronoUnit.SECONDS);
        return differenceIsSeconds > maxTimeOfLiveInSeconds;
    }

    private void calculateAndUpdateSum(final Double transactionMetricAmount) {
        final Double sum = transactionStatistics.getSum() + transactionMetricAmount;
        this.transactionStatistics.setSum(sum);
    }

    private void calculateAndUpdateMax(final Double transactionMetricAmount) {
        final Double currentMax = transactionStatistics.getMax();
        if (transactionMetricAmount > currentMax) {
            this.transactionStatistics.setMax(transactionMetricAmount);
        }
    }

    private void calculateAndUpdateMin(final Double transactionMetricAmount) {
        final Double currentMin = transactionStatistics.getMin();
        if (transactionMetricAmount < currentMin) {
            this.transactionStatistics.setMin(transactionMetricAmount);
        }
    }

    private void calculateAndUpdateAVG() {
        int totalMetricCollected = this.inMemoryDatabase.isEmpty() ? 1 : inMemoryDatabase.size();
        Double resultingValue = this.transactionStatistics.getSum() / totalMetricCollected;
        double truncatedTwoDecimalCasesResult = Math.floor(resultingValue * 100) / 100;
        transactionStatistics.setAvg(truncatedTwoDecimalCasesResult);
    }


}
