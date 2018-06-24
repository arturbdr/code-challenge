package com.n26.challenge.usecase;

import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;
import com.n26.challenge.domain.TransactionMetric;
import com.n26.challenge.domain.TransactionStatistics;
import com.n26.challenge.domain.exception.OldTransactionMetricException;
import com.n26.challenge.gateway.TransactionStoreGateway;
import com.n26.challenge.template.TransactionMetricTemplate;
import com.n26.challenge.template.TransactionStatisticsTemplate;
import com.n26.challenge.usecase.impl.TransactionServiceImpl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static br.com.six2six.fixturefactory.Fixture.from;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceImplTest {

    @Mock
    private TransactionStoreGateway transactionStoreGateway;

    private TransactionServiceImpl transactionServiceImpl;

    @BeforeClass
    public static void setupClass() {
        FixtureFactoryLoader.loadTemplates("com.n26.challenge.template");
    }

    @Before
    public void setupTest() {
        transactionServiceImpl = new TransactionServiceImpl(transactionStoreGateway);
        ReflectionTestUtils.setField(transactionServiceImpl, "transactionLimitInSeconds", 60);
    }

    @Test
    public void shouldReturnTransactionOldExceptionDueToOldTransaction() {
        // GIVEN invalid transaction
        TransactionMetric invalidOldTransaction = from(TransactionMetric.class).gimme(TransactionMetricTemplate.INVALID_TRANSACTION_OLD_DATE);

        // WHEN I call the method to save it. It should throw an exception when try to add it
        thenThrownBy(() -> transactionServiceImpl.addTransactionMetric(invalidOldTransaction))
                .isExactlyInstanceOf(OldTransactionMetricException.class)
                .hasMessageContaining("The transaction specified ");

        // THEN the gateway should not be called
        verify(transactionStoreGateway, never()).saveNewMetric(any());
    }

    @Test
    public void shouldAddTransactionMetricSuccessfully() {
        // GIVEN a valid transaction
        TransactionMetric validTransaction = from(TransactionMetric.class).gimme(TransactionMetricTemplate.VALID_12_5);

        // WHEN I try to call the method to save it
        doNothing().when(transactionStoreGateway).saveNewMetric(validTransaction);
        transactionServiceImpl.addTransactionMetric(validTransaction);

        // THEN it should be created a new register successfully and the gateway should be called only once
        verify(transactionStoreGateway, only()).saveNewMetric(validTransaction);
        verify(transactionStoreGateway, times(1)).saveNewMetric(validTransaction);

    }

    @Test
    public void shouldGetTransactionMetricSuccessfully() {
        // GIVEN a valid transaction
        TransactionStatistics expectedTransactionStatistics = from(TransactionStatistics.class).gimme(TransactionStatisticsTemplate.VALID_12_5);

        // WHEN I try to retrieve the metrics
        when(transactionStoreGateway.getTransactionMetrics()).thenReturn(Optional.of(expectedTransactionStatistics));
        Optional<TransactionStatistics> transactionMetrics = transactionServiceImpl.getTransactionMetrics();

        // THEN it should return the metrics
        then(transactionMetrics).isNotEmpty();
        then(transactionMetrics.get()).isEqualToComparingFieldByField(expectedTransactionStatistics);
        verify(transactionStoreGateway, only()).getTransactionMetrics();
        verify(transactionStoreGateway, times(1)).getTransactionMetrics();
    }

    @Test
    public void shouldCleanAllOldTransactions() {
        // WHEN I try to call retrieve the metrics
        doNothing().when(transactionStoreGateway).removeOldMetrics(anyInt());
        transactionServiceImpl.cleanUpOldTransactions();

        // THEN it should be created a new register successfully and the gateway should be called only once
        verify(transactionStoreGateway, only()).removeOldMetrics(60);
        verify(transactionStoreGateway, times(1)).removeOldMetrics(60);

    }
}