package com.mooc.service;

import com.mooc.exception.ResourceNotFoundException;
import com.mooc.model.User;
import com.mooc.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("User", id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public User updateProfile(Long id, String name) {
        User user = findById(id);
        if (name != null && !name.isBlank()) {
            user.setName(name);
        }
        return userRepository.save(user);
    }

    public void deactivate(Long id) {
        User user = findById(id);
        user.setActive(false);
        userRepository.save(user);
    }

    public User changeRole(Long id, User.Role role) {
        User user = findById(id);
        user.setRole(role);
        return userRepository.save(user);
    }
}
