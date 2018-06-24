package com.n26.challenge.gateway;

import com.n26.challenge.domain.TransactionMetric;
import com.n26.challenge.domain.TransactionStatistics;

import java.util.Optional;

public interface TransactionStoreGateway {
    void saveNewMetric(TransactionMetric newTransactionMetric);

    void removeOldMetrics(Integer maxTransactionsTimeOfLifeInSeconds);

    Optional<TransactionStatistics> getTransactionMetrics();
}
