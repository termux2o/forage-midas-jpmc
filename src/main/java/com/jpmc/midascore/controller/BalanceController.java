package com.jpmc.midascore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpmc.midascore.entity.Balance;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;

@RestController
@RequestMapping("/balance") // base path
public class BalanceController {

    @Autowired
    private UserRepository userRepository;

    // @GetMapping
    @GetMapping
    public Balance getBalance(@RequestParam("userId") long userId) {
        return userRepository.findById(userId)
                .map(user -> new Balance(user.getBalance()))
                .orElse(new Balance(0.0));
    }

    
}
