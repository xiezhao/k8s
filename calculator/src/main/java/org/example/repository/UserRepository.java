package org.example.repository;

import org.example.config.EntityState;
import org.example.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserRepository  extends JpaRepository<User, String>,
        JpaSpecificationExecutor<User> {


    List<User> findByUserTypeInAndEntityState(List<User.UserType> userTypes, EntityState entityState);

    User findByIdCardAndEntityState(String idCard, EntityState entityState);

    List<User> findByEntityState(EntityState entityState);

    List<User> findByDepartureTimeBetween(LocalDate start, LocalDate end);

}
