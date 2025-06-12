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

            Struct structSource = (Struct) sourceRecordChangeValue.get("source");

            String table = structSource.get("table").toString();

            System.out.println("I am ! " + table);
            if (table.equals("sale_sale_order")) {

                Struct struct = (Struct) sourceRecordChangeValue.get("after");
                String id = (String) struct.get("id").toString();
                String delivery_address_str = (String) struct.get("delivery_address_str").toString();
                String in_tax_total = (String) struct.get("in_tax_total").toString();
                String link = "http://localhost:8081/#/ds/sc.root.sale.quotations/edit/" + id;

                text = "SaleOrder with id " + id + " is created and delivey address is " + delivery_address_str + ". Total order value is" + in_tax_total + ". The link of saleorder is " + link;

                System.err.println(text);
                openAIService2.getAndSaveEmbedding(text, id);
                System.err.println(sourceRecordChangeValue);
            }

            if (table.equals("base_product")) {
                Struct struct = (Struct) sourceRecordChangeValue.get("after");
                String id = (String) struct.get("id").toString();
                String code = (String) struct.get("code").toString();
                String fullName = (String) struct.get("full_name").toString();
                String name = (String) struct.get("name").toString();
                String purchasePrice = (String) struct.get("purchase_price").toString();
                String salePrice = (String) struct.get("sale_price").toString();

                String link = "http://localhost:8081/#/ds/sc.root.sale.products/edit/" + id;

                text = "Product with id " + id + "present and product code is " + code + ". The name of product is" + name + ". the full name is " + fullName + ". the purchase prise is "
                        + purchasePrice + "and sale prise is " + salePrice + ". the link of product is " + link;

                System.err.println(text);
                openAIService2.generateAndSaveProductObjectEmbedding(text, id);
            }

            if (table.equals("base_partner")) {
                Struct struct = (Struct) sourceRecordChangeValue.get("after");
                String id = (String) struct.get("id").toString();
                String fullName = (String) struct.get("full_name").toString();
                String name = (String) struct.get("name").toString();

                String link = "http://localhost:8081/#/ds/sc.root.sale.customers/edit/" + id;

                text = "Customer with id " + id + "present and name of Customer is" + name + ". Customer full name is " + fullName + ". the link of Customer is " + link;

                System.err.println(text);
                openAIService2.generateAndSavePartnerObjectEmbedding(text, id);
            }


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