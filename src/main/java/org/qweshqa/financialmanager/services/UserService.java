package org.qweshqa.financialmanager.services;

import org.qweshqa.financialmanager.models.User;
import org.qweshqa.financialmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findUserById(int id){
        return userRepository.findById(id);
    }

    public Optional<User> findUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    @Transactional()
    public void save(User user){
        userRepository.save(user);
    }
}
