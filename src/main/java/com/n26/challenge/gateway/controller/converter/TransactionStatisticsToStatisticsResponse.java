package com.n26.challenge.gateway.controller.converter;

import com.n26.challenge.domain.TransactionStatistics;
import com.n26.challenge.gateway.controller.contract.StatisticsResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts a TransactionStatistics (domain object) into a StatisticsResponse (contract object)
 */
@Component
public class TransactionStatisticsToStatisticsResponse implements Converter<TransactionStatistics, StatisticsResponse> {

    @Override
    public StatisticsResponse convert(TransactionStatistics source) {
        return StatisticsResponse.builder()
                .avg(source.getAvg())
                .count(source.getCount())
                .max(source.getMax())
                .min(source.getMin())
                .sum(source.getSum())
                .build();
    }
}
