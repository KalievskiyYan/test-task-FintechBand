package com.test.service;

import com.test.domain.PaymentRequest;
import com.test.domain.PaymentRequestStatus;

import java.util.List;

/**
 * Service for managing payment request data
 */
public interface PaymentRequestService {
    /**
     * Create payment request if duplicate does not exist in repository
     *
     * @param paymentRequest payment request
     * @return id of created payment request
     */
    Long createPaymentRequest(PaymentRequest paymentRequest);

    /**
     * Get payment request status by ID
     *
     * @param id payment request ID
     * @return payment request status
     */
    PaymentRequestStatus getPaymentRequestStatus(Long id);

    /**
     * Get payment requests by client ID
     *
     * @param clientId client id
     * @return list of payment requests
     */
    List<PaymentRequest> getPaymentRequestsByClientId(Long clientId);
}
