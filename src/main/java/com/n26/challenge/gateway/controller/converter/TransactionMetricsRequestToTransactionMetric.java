package com.n26.challenge.gateway.controller.converter;

import com.n26.challenge.domain.TransactionMetric;
import com.n26.challenge.gateway.controller.contract.TransactionMetricsRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Converts a TransactionMetricsRequest to the Domain object TransactionMetric
 */
@Component
public class TransactionMetricsRequestToTransactionMetric implements Converter<TransactionMetricsRequest, TransactionMetric> {

    @Override
    public TransactionMetric convert(TransactionMetricsRequest source) {
        return TransactionMetric.builder()
                .amount(source.getAmount())
                .time(millisToLocalDateTime(source.getTimestamp()))
                .build();
    }

    private LocalDateTime millisToLocalDateTime(long millis) {
        final Instant instant = Instant.ofEpochMilli(millis);
        return instant.atZone(ZoneOffset.UTC).toLocalDateTime();
    }
}
