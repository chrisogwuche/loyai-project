package com.loyai.loyaiproject.repository;

import com.loyai.loyaiproject.model.Wins;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WinsRepository extends JpaRepository<Wins,Long> {

    @Query("SELECT sum(amountWon) from Wins")
    public Long sumAmountWon();
}
