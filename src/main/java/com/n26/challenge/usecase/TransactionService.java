package com.n26.challenge.usecase;

import com.n26.challenge.domain.TransactionMetric;
import com.n26.challenge.domain.TransactionStatistics;

import java.util.Optional;

public interface TransactionService {
    void addTransactionMetric(TransactionMetric transactionMetric);

    Optional<TransactionStatistics> getTransactionMetrics();
}
