package com.example.owetracker.repository;



import com.example.owetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);  // Custom query to find a user by username
    List<User> findByUsernameContainingIgnoreCase(String query);

}
