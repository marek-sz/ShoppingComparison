package com.example.shoppingcomparison.repository;

import com.example.shoppingcomparison.auth.ApplicationUserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationUserRepository extends JpaRepository<ApplicationUserDetails, Long> {
    ApplicationUserDetails findApplicationUserDetailsByUsername(String username);
}
