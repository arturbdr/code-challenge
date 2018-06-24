package com.n26.challenge.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TransactionStatistics {
    private Double sum;
    private Double avg;
    private Double max;
    private Double min;
    private Long count;

    public TransactionStatistics() {
        this.sum = 0D;
        this.avg = 0D;
        this.max = Double.MIN_VALUE;
        this.min = Double.MAX_VALUE;
        this.count = 0L;
    }

    public TransactionStatistics copy() {
        return TransactionStatistics.builder()
                .avg(this.avg)
                .sum(this.sum)
                .max(this.max)
                .min(this.min)
                .count(this.count)
                .build();
    }
}
