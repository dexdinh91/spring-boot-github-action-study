package com.example.github.action.demo.repository;

import com.example.github.action.demo.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {

    public List<User> findAll() {
        return List.of(
                new User(1L, "Knock Dinh 1", "1234567890"),
                new User(2L, "Knock Dinh 2", "0987654321"),
                new User(3L, "Knock Dinh 3", "5551234567")
        );
    }

    public User findById(Long id) {
        return findAll().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}