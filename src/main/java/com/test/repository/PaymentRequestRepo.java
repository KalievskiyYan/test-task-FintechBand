package com.test.repository;

import com.test.domain.PaymentRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Represents repository for payment requests data handling
 */
@Repository
public interface PaymentRequestRepo extends CrudRepository<PaymentRequest, Long> {
    /**
     * Retrieve payment request by ticket id
     *
     * @param id ticket id
     * @return {@link Optional} of payment request
     */
    @Query("select p from PaymentRequest p where p.ticketId = :ticketId")
    Optional<PaymentRequest> getRequestByTicketId(@Param("ticketId") Integer id);
}
