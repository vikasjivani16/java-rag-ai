package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;

@Configuration
public class DebeziumConnectorConfig {

    @Bean
    public io.debezium.config.Configuration customerConnector(Environment env) throws IOException {
        // var offsetStorageTempFile = File.createTempFile("offsets_", ".dat");
        // System.err.println("Hello world...!");
        // return io.debezium.config.Configuration.create().with("name", "customer_postgres_connector").with("connector.class",
        // "io.debezium.connector.postgresql.PostgresConnector")
        // .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore").with("offset.storage.file.filename",
        // offsetStorageTempFile.getAbsolutePath())
        // .with("offset.flush.interval.ms", "60000").with("database.hostname",
        // env.getProperty("slave.datasource.host")).with("database.port", env.getProperty("slave.datasource.port"))
        // .with("database.user", env.getProperty("slave.datasource.username")).with("database.password",
        // env.getProperty("slave.datasource.password"))
        // .with("database.dbname", env.getProperty("slave.datasource.database")).with("database.server.id",
        // "10181").with("database.server.name", "customer-postgres-db-server")
        // .with("topic.prefix", "slave-postgresql").with("database.history", "io.debezium.relational.history.MemoryDatabaseHistory")
        // .with("table.include.list", "public.base_unit")
        // .with("column.include.list", "public.base_unit")
        // // .with("publication.autocreate.mode", "all_tables")
        // .with("plugin.name", "pgoutput").with("slot.name", "dbz_customerdb_listener").build();

        var offsetStorageTempFile = File.createTempFile("offsets_", ".dat");
        System.err.println("Hello world...!");
        return io.debezium.config.Configuration.create().with("name", "customer_postgres_connector").with("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
                .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore").with("offset.storage.file.filename", offsetStorageTempFile.getAbsolutePath())
                .with("offset.flush.interval.ms", "60000").with("database.hostname", env.getProperty("slave.datasource.host")).with("database.port", env.getProperty("slave.datasource.port"))
                .with("database.user", env.getProperty("slave.datasource.username")).with("database.password", env.getProperty("slave.datasource.password"))
                .with("database.dbname", env.getProperty("slave.datasource.database")).with("database.server.id", "10181").with("database.server.name", "customer-postgres-db-server")
                .with("topic.prefix", "slave-postgresql").with("database.history", "io.debezium.relational.history.MemoryDatabaseHistory")
                .with("table.include.list", "public.sale_sale_order,public.base_product,public.base_partner")
                .with("plugin.name", "pgoutput").with("slot.name", "dbz_customerdb_listener_4").build();

    }
}