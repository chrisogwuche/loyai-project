package com.loyai.loyaiproject.repository;

import com.loyai.loyaiproject.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users,Long> {
    boolean existsByUserId (String userId);

    @Query(value = "SELECT sum (airtimeBought) FROM Users ")
    public Long sumAirtimeBought();

}
