package com.test.rest;

import com.test.rest.dto.PaymentRequestStatusDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * Generator of random status for payment request
 */
@RestController
@RequestMapping("/payment-status-generator")
public class PaymentStatusGenerator {
    private final Random random = new Random();

    /**
     * Get random payment request status
     *
     * @return payment request status
     */
    @GetMapping
    @ResponseBody
    public PaymentRequestStatusDTO getPaymentRequestStatus() {
        return PaymentRequestStatusDTO.values()[random.nextInt(PaymentRequestStatusDTO.values().length)];
    }
}
