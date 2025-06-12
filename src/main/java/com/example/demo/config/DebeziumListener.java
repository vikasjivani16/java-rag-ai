package com.example.demo.config;

import com.example.demo.service.OpenAIService2;
import io.debezium.config.Configuration;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


@Component
public class DebeziumListener {

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;
    private final OpenAIService2 openAIService2;

    public DebeziumListener(Configuration customerConnectorConfiguration, OpenAIService2 openAIService2) {
        System.err.println("I am DebeziumListener...!!");
        this.openAIService2 = openAIService2;
        this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class)).using(customerConnectorConfiguration.asProperties()).notifying(this::handleChangeEvent).build();
    }

    private void handleChangeEvent(RecordChangeEvent<SourceRecord> sourceRecordRecordChangeEvent) {
        System.err.println("I am DebeziumListener handleChangeEvent...!!");
        String text = "";
        var sourceRecord = sourceRecordRecordChangeEvent.record();

        System.err.println("Key = " + sourceRecord.key() + ", Value = " + sourceRecord.value());
        var sourceRecordChangeValue = (Struct) sourceRecord.value();
        System.err.println("SourceRecordChangeValue = " + sourceRecordChangeValue);
        if (sourceRecordChangeValue != null) {
            Struct struct = (Struct) sourceRecordChangeValue.get("after");
            String id = (String) struct.get("id").toString();
            String delivery_address_str = (String) struct.get("delivery_address_str").toString();
            // String sale_order_seq = (String) struct.get("sale_order_seq").toString();
            String in_tax_total = (String) struct.get("in_tax_total").toString();

            String link = "http://localhost:8081/#/ds/sc.root.sale.quotations/edit/" + id;

            text = "SaleOrder with id " + id + " is created and delivey address is " + delivery_address_str + ". Total order value is" + in_tax_total + ". The link of saleorder is " + link;

            System.err.println(text);
            openAIService2.getAndSaveEmbedding(text, Integer.parseInt(id));
            System.err.println(sourceRecordChangeValue);
        }
    }



    @PostConstruct
    private void start() {
        System.err.println("I am DebeziumListener start...!!");
        this.executor.execute(debeziumEngine);
    }

    @PreDestroy
    private void stop() throws IOException {
        if (Objects.nonNull(this.debeziumEngine)) {
            this.debeziumEngine.close();
        }
    }

}