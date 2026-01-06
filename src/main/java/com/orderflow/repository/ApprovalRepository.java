package com.orderflow.repository;

import com.orderflow.domain.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Approval entity operations.
 */
@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    List<Approval> findByOrderId(Long orderId);

    List<Approval> findByApprovedIsNull();
}
