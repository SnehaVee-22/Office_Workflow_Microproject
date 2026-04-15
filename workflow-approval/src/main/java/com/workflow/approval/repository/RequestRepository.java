package com.workflow.approval.repository;

import com.workflow.approval.entity.Request;
import com.workflow.approval.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findByRequestId(String requestId);
    List<Request> findByEmployeeOrderByCreatedAtDesc(User employee);
    List<Request> findByManagerAndStatusOrderByCreatedAtDesc(User manager, Request.Status status);
    @Query("SELECT r FROM Request r WHERE r.manager = :manager AND r.status IN ('APPROVED','REJECTED') AND r.decidedAt BETWEEN :start AND :end ORDER BY r.decidedAt DESC")
    List<Request> findManagerHistory(User manager, LocalDateTime start, LocalDateTime end);
    @Query("SELECT COUNT(r) FROM Request r WHERE r.status = :status")
    long countByStatus(Request.Status status);
    @Query("SELECT COUNT(r) FROM Request r WHERE r.manager = :manager AND r.status = :status")
    long countByManagerAndStatus(User manager, Request.Status status);
    boolean existsByRequestId(String requestId);
}
