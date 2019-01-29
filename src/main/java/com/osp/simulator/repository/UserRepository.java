package com.osp.simulator.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.osp.simulator.model.User;

/**
 * 
 * @author gibsona
 *
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername( String username );
}

