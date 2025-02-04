package com.votingforlunch.service;

import com.votingforlunch.model.User;
import com.votingforlunch.repository.UserRepository;
import com.votingforlunch.to.UserTo;
import com.votingforlunch.util.UserUtil;
import com.votingforlunch.util.ValidationUtil;
import com.votingforlunch.util.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

import static com.votingforlunch.util.UserUtil.prepareToSave;
import static com.votingforlunch.util.ValidationUtil.checkNotFoundWithId;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @CachePut(value = "users", key = "#user.email")
    public User create(User user) {
        Assert.notNull(user, "user must not be null");
        return userRepository.save(prepareToSave(user));
    }


    public User get(int id) {
        return checkNotFoundWithId(userRepository.getUserById(id), id);
    }

    @Cacheable("users")
    public Optional<User> findByEmailIgnoringCase(String email){
        Assert.notNull(email, "email must not be null");
        return ValidationUtil.checkNotFound(userRepository.findByEmailIgnoreCase(email), "email=" + email);

    }

    @CacheEvict(value = "users", allEntries = true)
    public void delete(int id) {

        checkNotFoundWithId(userRepository.delete(id),id);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public void update(UserTo userTo) {
        Assert.notNull(userTo, "user must not be null");
        User user = get(userTo.getId());
        prepareToSave(UserUtil.updateFromTo(user, userTo));
    }

    public void update(User user) {
        Assert.notNull(user, "user must not be null");
        userRepository.save(user);
    }

    @Transactional
    public void isEnable(int id, boolean enabled) {
        User user = userRepository.getUserById(id).orElse(null);
        if (user == null) {
            throw new NotFoundException("User with id " + id + " doesn't exists.");
        }
        user.setEnabled(enabled);
    }
}
