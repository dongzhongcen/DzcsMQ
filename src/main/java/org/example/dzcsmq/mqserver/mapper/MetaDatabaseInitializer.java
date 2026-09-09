package org.example.dzcsmq.mqserver.mapper;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class MetaDatabaseInitializer implements CommandLineRunner {
    private final MetaMapper metaMapper;

    public MetaDatabaseInitializer(MetaMapper metaMapper) {
        this.metaMapper = metaMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        Files.createDirectories(Path.of("date"));
        metaMapper.createExchangeableTable();
        metaMapper.createQueueTable();
        metaMapper.createBindingTable();
    }
}
