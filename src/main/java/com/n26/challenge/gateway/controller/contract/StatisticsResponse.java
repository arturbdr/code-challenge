package com.n26.challenge.gateway.controller.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@ApiModel(description = "Contains the transaction metrics")
public class StatisticsResponse {

    @ApiModelProperty(notes = "The total sum of transaction value in the last seconds", example = "1000")
    private Double sum;

    @ApiModelProperty(notes = "Average amount of transaction value in the last seconds", example = "100")
    private Double avg;

    @ApiModelProperty(notes = "Single highest transaction value in the last seconds", example = "200")
    private Double max;

    @ApiModelProperty(notes = "Single lowest transaction value in the last seconds", example = "50")
    private Double min;

    @ApiModelProperty(notes = "Total number of transactions happened in the last seconds", example = "10")
    private Long count;

}
