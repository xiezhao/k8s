package org.example.repository;

import org.example.config.EntityState;
import org.example.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, String>,
        JpaSpecificationExecutor<Attendance> {

    List<Attendance> findByYearAndMonthAndEntityState(Integer year, Integer month, EntityState entityState);

}
