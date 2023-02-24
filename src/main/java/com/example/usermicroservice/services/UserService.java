package com.example.usermicroservice.services;

import com.example.usermicroservice.models.User;
import com.example.usermicroservice.repositories.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    @LoadBalanced
    private RestTemplate restTemplate;

    public User getUserById(Long id) {
        Optional<User> optional;
        if ((optional = userRepository.findById(id)).isEmpty()) {
            return null;
        } else {
            return optional.get();
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

//    public User createNewUser(User newUser) {
//        newUser.setPassword(newUser.getPassword());
//        return userRepository.save(newUser);
//    }

    public ResponseEntity<User> createUser(@RequestBody User newUser) {
        // Use Eureka to discover the user microservice and make a POST request to create a new user
        ResponseEntity<User> response = restTemplate.postForEntity("http://USER-MICROSERVICE/user", newUser, User.class);
        return response;
    }

    public User updateUser(User updatedUser) {
        User user = userRepository.findByUsername(updatedUser.getUsername());
        BeanUtils.copyProperties(updatedUser, user);
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User createNewUser(User user) {
        user = userRepository.save(user);
        return user;
    }
}