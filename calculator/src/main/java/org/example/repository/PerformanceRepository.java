package org.example.repository;

import org.example.config.EntityState;
import org.example.domain.Performance;
import org.example.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, String>,
        JpaSpecificationExecutor<Performance> {


    List<Performance> findByUserTypeAndEntityState(User.UserType userType, EntityState entityState);

}
