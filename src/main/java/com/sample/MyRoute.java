package com.sample;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.univocity.UniVocityCsvDataFormat;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;

@Component
public class MyRoute extends RouteBuilder {


    @Override
    public void configure() throws Exception {

        UniVocityCsvDataFormat csvDf = new UniVocityCsvDataFormat();
        csvDf.setAsMap(true);
        csvDf.setHeaderExtractionEnabled(true);

        // @formatter:off
        rest("/")
            .post("/upload").consumes("text/html").bindingMode(RestBindingMode.off)
                .param()
                    .name("indexName").type(RestParamType.header).required(true).dataType("String")
                .endParam()
            .id("rest-upload-post")
            .to("direct:es-upload")
            ;

        from("file:upload?include=.*.csv&move=done&moveFailed=error")
            .routeId("file-polling")
            .log("uploading file: ${header.CamelFileNameOnly}")
            .setHeader("indexName").simple("${file:onlyname.noext}")
            .to("direct:es-upload")
            ;

        from("direct:es-upload")
            .routeId("es-upload")
            .setBody().simple("${body}\\n")
            .unmarshal(csvDf)
            .log("logForAnalysis -> sending ${body.size} events to ${header.indexName}")
            .to("elasticsearch://elasticsearch-sample?operation=BULK")
            .log("logForAnalysis -> done")
            ;
        // @formatter:on

    }
}
