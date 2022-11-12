package org.example.repository;

import org.example.domain.Sell;
import org.example.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SellRepository extends JpaRepository<Sell, String>,
        JpaSpecificationExecutor<Sell> {


    Sell findByUserAndYearAndMonth(User user, Integer year, Integer month);

}
