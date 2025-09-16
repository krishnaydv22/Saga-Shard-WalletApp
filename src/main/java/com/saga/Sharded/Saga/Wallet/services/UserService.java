package com.saga.Sharded.Saga.Wallet.services;

import com.saga.Sharded.Saga.Wallet.entity.User;
import com.saga.Sharded.Saga.Wallet.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor

public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user) {
        log.info("Creating user: {}", user.getEmail());
        User newUser = userRepository.save(user);
        log.info("User created with id {} in database shardwallet{}", newUser.getId(), (newUser.getId() % 2 + 1));
        //odd in shardwallet2  and even in shardwallet1
        return newUser;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }


}
