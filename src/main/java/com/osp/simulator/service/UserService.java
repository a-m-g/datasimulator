package com.osp.simulator.service;

import java.util.List;

import com.osp.simulator.model.User;

/**
 * 
 * @author gibsona
 *
 */
public interface UserService {
    public User findById(Long id);
    public User findByUsername(String username);
    public List<User> findAll();
    public void updateUser(User user);
}
