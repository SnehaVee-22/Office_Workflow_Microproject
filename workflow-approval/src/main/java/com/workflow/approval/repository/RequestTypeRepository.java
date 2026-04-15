package com.workflow.approval.repository;

import com.workflow.approval.entity.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RequestTypeRepository extends JpaRepository<RequestType, Long> {
    Optional<RequestType> findByName(String name);
    boolean existsByName(String name);
}
