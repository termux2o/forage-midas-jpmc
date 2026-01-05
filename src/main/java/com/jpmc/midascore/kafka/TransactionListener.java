package com.jpmc.midascore.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.jpmc.midascore.config.GenralProperties;
import com.jpmc.midascore.foundation.Transaction;

import java.util.ArrayList;
import java.util.List;

@Component
public class TransactionListener {

    private final GenralProperties properties;
    private final List<Float> firstFourAmounts = new ArrayList<>();

    public TransactionListener(GenralProperties properties) {
        this.properties = properties;
    }

    @KafkaListener(
        topics = "#{genralProperties.kafkaTopic}",
        groupId = "midas-core"
    )
    public void listen(Transaction transaction) {
        if (firstFourAmounts.size() < 4) {
            firstFourAmounts.add(transaction.getAmount());
            System.out.println("Received amount: " + transaction.getAmount());
        }
    }

    // Optional: helper method to get first four amounts after listener runs
    public List<Float> getFirstFourAmounts() {
        return firstFourAmounts;
    }
}
