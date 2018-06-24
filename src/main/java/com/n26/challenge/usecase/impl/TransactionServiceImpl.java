package com.n26.challenge.usecase.impl;

import com.n26.challenge.domain.TransactionMetric;
import com.n26.challenge.domain.TransactionStatistics;
import com.n26.challenge.domain.exception.OldTransactionMetricException;
import com.n26.challenge.gateway.TransactionStoreGateway;
import com.n26.challenge.usecase.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    @Value("${app.transactionLimitInSeconds}")
    private Integer transactionLimitInSeconds;

    private final TransactionStoreGateway transactionStoreGateway;

    /**
     * If the transaction is not old (the moment it happened should not be greater than a parametrized time), it will be
     * collected and inserted for the api metric. Every new
     *
     * @param transactionMetric - containing the information about the transation to be inserted
     */
    @Override
    public void addTransactionMetric(final TransactionMetric transactionMetric) {
        if (isTransactionMetricOlderThan(transactionMetric.getTime())) {
            log.error("Invalid transaction metric received {}. Old date", transactionMetric);
            final String errorMessage = String.format("The transaction specified %s is older than %s seconds " +
                    "and will not be collected", transactionMetric.getTime(), transactionLimitInSeconds);
            throw new OldTransactionMetricException(errorMessage);
        }
        log.info("storing new transaction {}", transactionMetric);
        transactionStoreGateway.saveNewMetric(transactionMetric);
    }

    /**
     * Checks if the given transaction time is older than a parametrized time.
     *
     * @param transactionTime - Representing when the transaction has happened
     * @return true if it is older than parametrized value seconds. False otherwise.
     */
    private boolean isTransactionMetricOlderThan(final LocalDateTime transactionTime) {
        long differenceInSeconds = transactionTime.until(LocalDateTime.now(ZoneOffset.UTC), ChronoUnit.SECONDS);
        return differenceInSeconds > transactionLimitInSeconds;
    }

    /**
     * Retrieve the metrics collected and calculated so far.
     * @return TransactionStatistics with the metrics calculated
     */
    @Override
    public Optional<TransactionStatistics> getTransactionMetrics() {
        return transactionStoreGateway.getTransactionMetrics();
    }

    /**
     * In a database this could be done automatically by setting the TTL
     * when inserting a new register.
     * This job will be triggered and will remove all old transactions.
     */
    @Scheduled(fixedDelayString = "${app.scheduled.cleanUpOldTransactions}")
    public void cleanUpOldTransactions() {
        transactionStoreGateway.removeOldMetrics(transactionLimitInSeconds);
    }
}
