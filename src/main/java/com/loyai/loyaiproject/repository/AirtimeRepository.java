package com.loyai.loyaiproject.repository;

import com.loyai.loyaiproject.model.Airtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AirtimeRepository extends JpaRepository<Airtime,Long> {
    @Query(value = "SELECT sum (airtimeBought) FROM Airtime ")
    public Long sumAirtimeBought();
}
