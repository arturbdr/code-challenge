package com.n26.challenge.gateway.controller;

import com.n26.challenge.domain.TransactionMetric;
import com.n26.challenge.domain.TransactionStatistics;
import com.n26.challenge.gateway.controller.contract.StatisticsResponse;
import com.n26.challenge.gateway.controller.contract.TransactionMetricsRequest;
import com.n26.challenge.gateway.controller.converter.TransactionMetricsRequestToTransactionMetric;
import com.n26.challenge.gateway.controller.converter.TransactionStatisticsToStatisticsResponse;
import com.n26.challenge.usecase.TransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

import static com.n26.challenge.gateway.controller.mapping.URLMapping.ADD_TRANSACTIONS;
import static com.n26.challenge.gateway.controller.mapping.URLMapping.GET_STATISTICS;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@Api("Endpoints to collect new transaction metrics and retrieve last 60 seconds status")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMetricsRequestToTransactionMetric transactionMetricsRequestToTransactionMetric;
    private final TransactionStatisticsToStatisticsResponse transactionStatisticsToStatisticsResponse;

    @PostMapping(value = ADD_TRANSACTIONS, consumes = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("Collect a new transaction metric")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The transaction was collected successfully"),
            @ApiResponse(code = 204, message = "The transaction is older than 60 seconds and it was not collected")
    })
    public ResponseEntity<Void> addTransactionMetric(@RequestBody @Valid final TransactionMetricsRequest transactionMetricsRequest) {

        TransactionMetric transactionMetric = transactionMetricsRequestToTransactionMetric.convert(transactionMetricsRequest);
        transactionService.addTransactionMetric(transactionMetric);
        return ResponseEntity
                .status(CREATED)
                .build();
    }

    @GetMapping(value = GET_STATISTICS, produces = APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("Retrieve last seconds API statuses.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Return the API Statistics"),
            @ApiResponse(code = 204, message = "If there are no metrics at this time")
    })
    public ResponseEntity<StatisticsResponse> getStatistics() {
        Optional<TransactionStatistics> transactionMetrics = transactionService.getTransactionMetrics();
        return transactionMetrics
                .map(transactionStatistics -> ResponseEntity.ok(transactionStatisticsToStatisticsResponse.convert(transactionStatistics)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
