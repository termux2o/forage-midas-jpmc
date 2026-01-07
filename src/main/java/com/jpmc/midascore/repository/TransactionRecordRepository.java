package com.jpmc.midascore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {
               //    public interface TransactionRecordRepository extends CrudRepository<UserRecord, Long>
               // What it does

               // Declares a repository for:

               // Entity: TransactionRecord

               // What Spring gives you automatically

               // Without writing any code, you get:
               // save(UserRecord user)
               // findAll()
               // deleteById(Long id)
               // existsById(Long id)


}