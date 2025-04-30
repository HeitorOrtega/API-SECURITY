package br.com.fiap.apisecurity.service;

import br.com.fiap.apisecurity.model.User;
import br.com.fiap.apisecurity.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //CRUD

    //CREATE
    @Transactional
    @CachePut(value = "users" , key = "#results.id")
    public User createUser(User user) {
        return userRepository.save(user);
    }

    //Read ID
    @Cacheable(value = "users" , key = "#id")
    public User findUserById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }
    //Read all
    @Cacheable(value = "users" , key = "'all'")
    public List<User> readAllUsers() {
        return userRepository.findAll();
    }

    //Update
    @Transactional
    @CachePut(value = "users" , key = "#results.id")
    public User updateUser(UUID id, User user) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User updatedUser = userOptional.get();
        }
        user.setId(id);
        return userRepository.save(user);
    }
    //Delete
    @Transactional
    @CacheEvict(value = "users" , key = "#id")
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
        cleanCacheOfAllUsers();
        //cleanAllUserFromCache();
    }

    //Metodos auxiliares do CacheEvict
    //Atualiza o cache
    @CacheEvict(value = "users", key = "'all'" )
    public void cleanCacheOfAllUsers() {}

    //Apaga todos os usuarios do ache
    @CacheEvict(value = "users", allEntries = true )
    public void cleanAllUserFromCache() {}

}
