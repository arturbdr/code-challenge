package com.n26.challenge.gateway.controller.converter;

import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;
import com.n26.challenge.domain.TransactionMetric;
import com.n26.challenge.gateway.controller.contract.TransactionMetricsRequest;
import com.n26.challenge.template.TransactionMetricsRequestTemplate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;

import static br.com.six2six.fixturefactory.Fixture.from;
import static org.assertj.core.api.BDDAssertions.then;

public class TransactionMetricsRequestToTransactionMetricTest {

    private TransactionMetricsRequestToTransactionMetric transactionMetricsRequestToTransactionMetric;


    @BeforeClass
    public static void setupClass() {
        FixtureFactoryLoader.loadTemplates("com.n26.challenge.template");
    }

    @Before
    public void setupTest() {
        transactionMetricsRequestToTransactionMetric = new TransactionMetricsRequestToTransactionMetric();
    }

    @Test
    public void shouldConvertATransactionMetricsRequestToTransactionMetric() {
        // GIVEN a requestObject to be converted
        TransactionMetricsRequest transactionMetricsRequest =
                from(TransactionMetricsRequest.class).gimme(TransactionMetricsRequestTemplate.INVALID_REQUEST_OLD_2016);

        // WHEN I try to convert the object
        TransactionMetric transactionMetricConverted = transactionMetricsRequestToTransactionMetric.convert(transactionMetricsRequest);

        // THEN it should be converted successfully
        then(transactionMetricConverted.getTime()).isEqualTo(LocalDateTime.of(2016, 11, 3, 16, 56, 44));
        then(transactionMetricConverted.getAmount()).isEqualTo(12.3);
    }

    @Test
    public void shouldConvertATransactionMetricsRequestToTransactionMetricActual() {
        // GIVEN a requestObject to be converted
        TransactionMetricsRequest transactionMetricsRequest =
                from(TransactionMetricsRequest.class).gimme(TransactionMetricsRequestTemplate.ACTUAL_EPOCH_MILLIS);

        // WHEN I try to convert the object
        TransactionMetric transactionMetricConverted = transactionMetricsRequestToTransactionMetric.convert(transactionMetricsRequest);

        // THEN it should be converted successfully
        then(transactionMetricConverted.getTime()).isEqualTo(LocalDateTime.of(2018, 6, 24, 6, 24, 54));
        then(transactionMetricConverted.getAmount()).isEqualTo(12.3);
    }


}