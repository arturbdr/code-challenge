package com.n26.challenge.gateway.inmemorydb;

import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;
import com.n26.challenge.domain.TransactionMetric;
import com.n26.challenge.domain.TransactionStatistics;
import com.n26.challenge.template.TransactionMetricTemplate;
import com.n26.challenge.template.TransactionStatisticsTemplate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.six2six.fixturefactory.Fixture.from;
import static org.assertj.core.api.BDDAssertions.then;

public class InMemoryTransactionStoreGatewayImplTest {

    private InMemoryTransactionStoreGatewayImpl gatewayImpl;
    private TransactionStatistics transactionStatistics;
    private List<TransactionMetric> inMemoryDatabase;

    @BeforeClass
    public static void setupClass() {
        FixtureFactoryLoader.loadTemplates("com.n26.challenge.template");
    }

    @Before
    public void setUp() {
        gatewayImpl = new InMemoryTransactionStoreGatewayImpl();
        transactionStatistics =
                (TransactionStatistics) ReflectionTestUtils.getField(gatewayImpl, "transactionStatistics");
        inMemoryDatabase =
                (List<TransactionMetric>) ReflectionTestUtils.getField(gatewayImpl, "inMemoryDatabase");
    }

    @Test
    public void shouldSaveSingleMetricSuccessfully() {
        // Given a valid transaction
        TransactionMetric validTractionMetric = from(TransactionMetric.class).gimme(TransactionMetricTemplate.VALID_12_5);

        // WHEN I try to save it
        gatewayImpl.saveNewMetric(validTractionMetric);

        // THEN it should be saved successfully and the metrics should be updated with the values below
        TransactionStatistics expectedTransactionStatistics = from(TransactionStatistics.class).gimme(TransactionStatisticsTemplate.VALID_12_5);

        then(transactionStatistics).isEqualTo(expectedTransactionStatistics);
        then(inMemoryDatabase).containsOnly(validTractionMetric);
        then(gatewayImpl.getTransactionMetrics().get()).isEqualTo(expectedTransactionStatistics);
    }


    @Test
    public void shouldSave100000EqualsMetricsSuccessfully() {
        // Given 100000 valid transaction
        List<TransactionMetric> validTransactions = from(TransactionMetric.class).gimme(100000, TransactionMetricTemplate.VALID_12_5);

        // WHEN I try to save each transaction
        validTransactions.parallelStream()
                .forEach(gatewayImpl::saveNewMetric);

        // THEN it should be saved successfully and the metrics should be updated with the values below
        TransactionStatistics expectedTransactionStatistics = TransactionStatistics
                .builder()
                .avg(12.5)
                .count(100000L)
                .sum(1250000D)
                .max(12.5)
                .min(12.5)
                .build();

        then(transactionStatistics).isEqualTo(expectedTransactionStatistics);
        then(gatewayImpl.getTransactionMetrics().get()).isEqualTo(expectedTransactionStatistics);
        then(inMemoryDatabase).containsAll(validTransactions);
    }

    @Test
    public void shouldReturnOptionalEmptyWhenNoMetricsExists() {
        // Given no transaction

        // WHEN I try to retrieve metrics
        Optional<TransactionStatistics> transactionMetrics = gatewayImpl.getTransactionMetrics();

        // THEN it should be an Empty Optional
        then(transactionMetrics).isEmpty();
    }

    @Test
    public void shouldCalculateTheMetricsAfterRemoveOldTransactions() {
        // Given 100 valid transaction
        List<TransactionMetric> validTransactions = from(TransactionMetric.class).gimme(100, TransactionMetricTemplate.VALID_12_5);

        // AND 50 invalid (old) transactions
        List<TransactionMetric> invalidOldTransactions = from(TransactionMetric.class).gimme(50, TransactionMetricTemplate.INVALID_TRANSACTION_OLD_DATE_10_5);

        List<TransactionMetric> allTransactions = Stream.concat(validTransactions.stream(), invalidOldTransactions.stream()).collect(Collectors.toList());

        // AND I save all the transaction to generate the transactions
        allTransactions.parallelStream()
                .forEach(gatewayImpl::saveNewMetric);

        // THEN it should be saved successfully and the metrics should be updated with the values below
        TransactionStatistics expectedTransactionStatistics = TransactionStatistics
                .builder()
                .avg(11.83)
                .count(150L)
                .sum(1775D)
                .max(12.5)
                .min(10.5)
                .build();
        then(transactionStatistics).isEqualTo(expectedTransactionStatistics);

        // WHEN I try to remove the invalid (old) transactions
        gatewayImpl.removeOldMetrics(60);

        // THEN it should be saved successfully and the metrics should be updated with the values below
        TransactionStatistics expectedTransactionStatisticsAfterCleanup = TransactionStatistics
                .builder()
                .avg(12.5)
                .count(100L)
                .sum(1250D)
                .max(12.5)
                .min(12.5)
                .build();
        then(transactionStatistics).isEqualTo(expectedTransactionStatisticsAfterCleanup);
    }

}
