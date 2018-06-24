package com.n26.challenge.template;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import com.n26.challenge.gateway.controller.contract.StatisticsResponse;

public class StatisticsResponseTemplate implements TemplateLoader {

    public static String VALID_12_5 = "valid_12_5";

    @Override
    public void load() {
        Fixture.of(StatisticsResponse.class)
                .addTemplate(VALID_12_5, new Rule() {{
                    add("avg", 12.5);
                    add("count", 1L);
                    add("sum", 12.5);
                    add("max", 12.5);
                    add("min", 12.5);
                }});
    }
}
