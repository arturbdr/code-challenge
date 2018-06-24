package com.n26.challenge.gateway.controller;

import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.challenge.gateway.controller.contract.StatisticsResponse;
import com.n26.challenge.gateway.controller.contract.TransactionMetricsRequest;
import com.n26.challenge.template.TransactionMetricsRequestTemplate;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static br.com.six2six.fixturefactory.Fixture.from;
import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class TransactionControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeClass
    public static void setupClass() {
        FixtureFactoryLoader.loadTemplates("com.n26.challenge.template");
    }

    @Test
    public void shouldAddANewTransactionMetricSuccessfully() throws Exception {
        // GIVEN a valid transaction request
        TransactionMetricsRequest transactionMetricsRequest =
                from(TransactionMetricsRequest.class).gimme(TransactionMetricsRequestTemplate.VALID_REQUEST);
        String jsonTransactionRequest = objectMapper.writeValueAsString(transactionMetricsRequest);

        // AND a valid Header and Body
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE);
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonTransactionRequest, headers);

        // WHEN I try to this transaction register
        ResponseEntity<Void> responseEntity = testRestTemplate
                .exchange("/transactions", POST, httpEntity, Void.class);

        // THEN it should return 201 - The transaction was created
        then(responseEntity.getStatusCode()).isEqualTo(CREATED);
    }

    @Test
    public void shouldAdd1000TransactionMetricSuccessfullyAndRetrieveMetrics() {
        // GIVEN 10000 valid transactions request
        List<TransactionMetricsRequest> transactionMetricsRequestList = from(TransactionMetricsRequest.class).gimme(1000, TransactionMetricsRequestTemplate.VALID_REQUEST);
        transactionMetricsRequestList.parallelStream()
                .forEach(transactionMetricsRequest -> {
                    String jsonTransactionRequest = null;
                    try {
                        jsonTransactionRequest = objectMapper.writeValueAsString(transactionMetricsRequest);
                    } catch (JsonProcessingException e) {
                        Assert.fail();
                    }

                    // AND a valid Header and Body
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE);
                    HttpEntity<String> httpEntity = new HttpEntity<>(jsonTransactionRequest, headers);

                    // WHEN I try to this transaction register
                    ResponseEntity<Void> responseEntity = testRestTemplate
                            .exchange("/transactions", POST, httpEntity, Void.class);

                    // THEN it should return 201 - The transaction was created
                    then(responseEntity.getStatusCode()).isEqualTo(CREATED);
                });

        // WHEN I try to read the metrics after insert 1000 registers
        ResponseEntity<StatisticsResponse> responseEntity = testRestTemplate.getForEntity("/statistics", StatisticsResponse.class);

        StatisticsResponse expectedStatisticsResponse = StatisticsResponse.builder()
                .sum(12500D)
                .min(12.5)
                .max(12.5)
                .count(1000L)
                .avg(12.5)
                .build();

        // THEN it should return 200 with the metric with the following values
        then(responseEntity.getStatusCode()).isEqualTo(OK);
        then(responseEntity.getBody()).isEqualToComparingFieldByField(expectedStatisticsResponse);
    }

    @Test
    public void shouldReturn204DueToInvalidDateInTransactionMetric() throws Exception {
        // GIVEN an invalid transaction request
        TransactionMetricsRequest transactionMetricsRequest =
                from(TransactionMetricsRequest.class).gimme(TransactionMetricsRequestTemplate.INVALID_REQUEST_OLD);
        String jsonTransactionRequest = objectMapper.writeValueAsString(transactionMetricsRequest);

        // AND a valid Header and Body
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE);
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonTransactionRequest, headers);

        // WHEN I try to this transaction register
        ResponseEntity<Void> responseEntity = testRestTemplate
                .exchange("/transactions", POST, httpEntity, Void.class);

        // THEN it should return 204 - The request was rejected
        then(responseEntity.getStatusCode()).isEqualTo(NO_CONTENT);
    }


}