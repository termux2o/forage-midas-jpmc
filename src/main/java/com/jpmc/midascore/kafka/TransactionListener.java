package com.jpmc.midascore.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.jpmc.midascore.config.GenralProperties;
import com.jpmc.midascore.foundation.Transaction;

@Component
public class TransactionListener {

    private final GenralProperties properties;
    
    public TransactionListener(GenralProperties properties) {
        this.properties = properties;
    }

    @KafkaListener(
        topics = "#{genralProperties.kafkaTopic}", // SpEL calls getKafkaTopic()
        groupId = "midas-core-task2-consmer-group" // can give any name
    )
    public void listen(Transaction transaction) {
        System.out.println("Received: " + transaction);
    }
}
