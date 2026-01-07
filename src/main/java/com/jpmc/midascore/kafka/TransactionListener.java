package com.jpmc.midascore.kafka;

import java.util.Optional;

import org.apache.kafka.clients.producer.internals.Sender;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.jpmc.midascore.config.GenralProperties;
import com.jpmc.midascore.entity.Incentive;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


@Component
public class TransactionListener {

    private final GenralProperties properties; // task1 vars

    private final UserRepository userRepository; //task2 vars
    private final TransactionRecordRepository transactionRepo; //task2 vars

    private final RestTemplate restTemplate; //task4 var

    public TransactionListener(GenralProperties properties, UserRepository userRepository,
            TransactionRecordRepository transactionRepo, RestTemplate restTemplate) 
    {
        this.properties = properties; // task 2 var initialize by constructor

        this.userRepository = userRepository; // task 3 var initialize by constructor
        this.transactionRepo = transactionRepo; // task 3 var initialize by constructor

        this.restTemplate =restTemplate; // task 4 var initialize by constructor
    }
    // Task2 Kafka listener

    // @KafkaListener(
    //     topics = "#{genralProperties.kafkaTopic}", // SpEL calls getKafkaTopic()
    //     groupId = "midas-core-task2-consmer-group" // can give any name
    // )
    // public void listen(Transaction transaction) {
    //     System.out.println("Received: " + transaction);
    // }

    //Task3 Validate Transaction

        //     2️⃣ What is a “transaction” (simple meaning)?

        // A transaction means:

        // ALL database changes succeed together
        // OR none of them happen.

        // No half-completed work.

        // 3️⃣ What happens WITHOUT @Transactional (problem)

        // Your method does multiple DB operations:

        // Deduct sender balance

        // Add recipient balance

        // Save sender

        // Save recipient

        // Save transaction record

        // Imagine this happens:

        // ✔ sender balance updated
        // ✔ sender saved
        // ❌ crash before recipient save


        // 💥 Result:

        // Sender lost money

        // Recipient didn’t get money

        // Transaction record missing

        // Database is CORRUPT

        // 4️⃣ What @Transactional DOES (magic explained)

        // With @Transactional:

        // Spring opens a DB transaction at method start

        // Tracks all DB changes

        // If everything succeeds → COMMIT

        // If ANY error occurs → ROLLBACK everything

        // Timeline:
        // BEGIN TRANSACTION
        // update sender
        // update recipient
        // save transaction
        // COMMIT


        // or if error:

        // BEGIN TRANSACTION
        // update sender
        // ❌ error
        // ROLLBACK (undo sender update)

    // define repositories for DB operations
    // private final UserRepository userRepository;
    // private final TransactionRecordRepository transactionRepo;


    // task 3
    @Transactional // see use of Transactional in Above text
    @KafkaListener
    (
        topics = "#{genralProperties.kafkaTopic}", // speeling is GenralProperties not GeneralProperties will evaluated as genralProperties 
        groupId = "midas-core-task3-cnsumer-group"
    )
    public void process(Transaction transaction) {
        

        long senderId = transaction.getSenderId();
        long recipientId = transaction.getRecipientId();

        UserRecord sender = userRepository.findById(senderId);
        UserRecord recipient = userRepository.findById(recipientId);

        // Validate sender & recipient
        if (sender == null || recipient == null) {
            return;
        }

        // Validate balance
        if (sender.getBalance() < transaction.getAmount()) {
            return;
        }

        // Update balances
        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(recipient.getBalance() + transaction.getAmount());

        // ✅ SAVE users (CRITICAL)
        userRepository.save(sender);
        userRepository.save(recipient);

        // Create transaction record
        TransactionRecord record = new TransactionRecord();
        record.setSender(sender);
        record.setRecipient(recipient);
        record.setAmount((double) transaction.getAmount()); // compatible dtype to save amount;
        // Save transaction
        transactionRepo.save(record);
        System.out.println("Waldorf balance: " +
        userRepository.findByName("waldorf").getBalance());
        UserRecord waldorf = userRepository.findByName("waldorf");
        System.out.println("Waldorf Id: " + (waldorf != null ? waldorf.getId() : "Not found"));

        


        // task 4
        // Incentive incentive=restTemplate.postForObject(
        //     url:"http://localhost:8080/incentive", record:transaction, responseType:Incentive.class
        // )
        // 1️⃣ Call the Incentive API with the transaction object
        Incentive incentive = restTemplate.postForObject(
            "http://localhost:8080/incentive", // URL of the Incentive API
            transaction,                        // send this Transaction object
            Incentive.class                     // expect response as Incentive class
        );
        // 2️⃣ Make sure the API returned something

        double incentiveAmount = 0.0;
        if (incentive != null) {                // check API returned a valid object
            incentiveAmount = incentive.getAmount(); // extract amount
        }           // default in case API fails
        recipient.setBalance((float) (recipient.getBalance()+incentiveAmount));
    }
}
