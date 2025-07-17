package com.hrms.project.repository;


import com.hrms.project.entity.VoterId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import static javax.swing.text.html.HTML.Tag.SELECT;
import static org.hibernate.grammars.hql.HqlParser.FROM;

public interface VoterIdRepository extends JpaRepository<VoterId,String> {


    Optional<VoterId> findByEmployee_EmployeeId(String employeeId);
    boolean existsByVoterIDNumber(String voterIDNumber);
}

