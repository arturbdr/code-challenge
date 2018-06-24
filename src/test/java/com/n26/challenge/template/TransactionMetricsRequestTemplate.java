package com.n26.challenge.template;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import com.n26.challenge.gateway.controller.contract.TransactionMetricsRequest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public class TransactionMetricsRequestTemplate implements TemplateLoader {

    public static final String VALID_REQUEST = "validRequest";
    public static final String INVALID_REQUEST_OLD = "invalidRequest";
    public static final String INVALID_REQUEST_OLD_2016 = "invalidRequest";
    public static final String ACTUAL_EPOCH_MILLIS = "actualEpochMillis";

    private static long nowFromUTC() {
        LocalDateTime time = LocalDateTime.now(ZoneOffset.UTC);
        return time.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    private static long oldFromUTC() {
        LocalDateTime time = LocalDateTime.now(ZoneOffset.UTC).minus(61, ChronoUnit.SECONDS);
        return time.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    @Override
    public void load() {
        Fixture.of(TransactionMetricsRequest.class)
                .addTemplate(INVALID_REQUEST_OLD, new Rule() {{
                    add("amount", 12.5);
                    add("timestamp", oldFromUTC());
                }})
                .addTemplate(INVALID_REQUEST_OLD_2016, new Rule() {{
                    add("amount", 12.3);
                    add("timestamp", 1478192204000L);
                }})
                .addTemplate(VALID_REQUEST, new Rule() {{
                    add("amount", 12.5);
                    add("timestamp", nowFromUTC());
                }})
                .addTemplate(ACTUAL_EPOCH_MILLIS, new Rule() {{
                    add("amount", 12.3);
                    add("timestamp", 1529821494000L);
                }});
    }

}
