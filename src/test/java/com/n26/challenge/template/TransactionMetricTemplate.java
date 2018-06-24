package com.n26.challenge.template;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import com.n26.challenge.domain.TransactionMetric;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public class TransactionMetricTemplate implements TemplateLoader {
    public static String INVALID_TRANSACTION_OLD_DATE_10_5 = "invalidOldDate10_5";
    public static String INVALID_TRANSACTION_OLD_DATE = "invalidOldDate";
    public static String VALID_12_5 = "valid12_5";

    @Override
    public void load() {
        Fixture.of(TransactionMetric.class)
                .addTemplate(INVALID_TRANSACTION_OLD_DATE, new Rule() {{
                    add("amount", 12.5);
                    add("time", LocalDateTime.now(ZoneOffset.UTC).minus(61, ChronoUnit.SECONDS));
                }})
                .addTemplate(INVALID_TRANSACTION_OLD_DATE_10_5, new Rule() {{
                    add("amount", 10.5);
                    add("time", LocalDateTime.now(ZoneOffset.UTC).minus(61, ChronoUnit.SECONDS));
                }})
                .addTemplate(VALID_12_5, new Rule() {{
                    add("amount", 12.5);
                    add("time", LocalDateTime.now(ZoneOffset.UTC));
                }});
    }
}
