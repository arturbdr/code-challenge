package com.n26.challenge.gateway.controller.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Representing the contract of a new transaction metric to be collected")
public class TransactionMetricsRequest {

    @NotNull(message = "The transaction amount is required ")
    @ApiModelProperty(notes = "Transaction amount. Is a double specifying the amount", example = "12.3", required = true)
    private Double amount;

    @NotNull(message = "The transaction timestamp is required ")
    @ApiModelProperty(notes = "Transaction time in epoch in millis in UTC " +
            "time zone. Is a Long specifying unix time format in milliseconds", example = "1478192204000", required = true)
    private Long timestamp;
}
