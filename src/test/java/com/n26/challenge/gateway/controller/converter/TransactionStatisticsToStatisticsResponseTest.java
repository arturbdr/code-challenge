package com.n26.challenge.gateway.controller.converter;

import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;
import com.n26.challenge.domain.TransactionStatistics;
import com.n26.challenge.gateway.controller.contract.StatisticsResponse;
import com.n26.challenge.template.StatisticsResponseTemplate;
import com.n26.challenge.template.TransactionStatisticsTemplate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static br.com.six2six.fixturefactory.Fixture.from;
import static org.assertj.core.api.BDDAssertions.then;

public class TransactionStatisticsToStatisticsResponseTest {

    private TransactionStatisticsToStatisticsResponse transactionStatisticsToStatisticsResponse;

    @BeforeClass
    public static void setupClass() {
        FixtureFactoryLoader.loadTemplates("com.n26.challenge.template");
    }

    @Before
    public void setupTest() {
        transactionStatisticsToStatisticsResponse = new TransactionStatisticsToStatisticsResponse();
    }

    @Test
    public void shouldConvertATransactionStatisticsToStatisticsResponse() {
        // Given a valid TransactionStatistics
        TransactionStatistics transactionStatistics = from(TransactionStatistics.class).gimme(TransactionStatisticsTemplate.VALID_12_5);

        //WHEN I try to convert to a StatisticsToStatisticsResponse
        StatisticsResponse result = transactionStatisticsToStatisticsResponse.convert(transactionStatistics);

        // THEN it should convert successfully
        StatisticsResponse statisticsResponseExpected = from(StatisticsResponse.class).gimme(StatisticsResponseTemplate.VALID_12_5);
        then(result).isEqualToComparingFieldByField(statisticsResponseExpected);
    }
}
