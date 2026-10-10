package com.college.lms.identity.repository;

import com.college.lms.identity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByGoogleId(String googleId);

    boolean existsByEmail(String email);

    /**
     * Find all users who have the given role name.
     * Used by admin listings.
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    Page<User> findByRoleName(@Param("roleName") String roleName, Pageable pageable);

    /**
     * Search users by name or email fragment (case-insensitive).
     */
    @Query("""
        SELECT u FROM User u
        WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%'))
        """)
    Page<User> search(@Param("query") String query, Pageable pageable);
}
