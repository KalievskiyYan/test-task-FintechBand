package com.test.rest;

import com.test.domain.PaymentRequest;
import com.test.rest.dto.PaymentRequestDTO;
import com.test.rest.dto.PaymentRequestStatusDTO;
import com.test.service.PaymentRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Payment request rest controller
 */
@RestController
@RequestMapping("/payment-request")
public class PaymentRequestController {
    private final PaymentRequestService paymentRequestService;

    @Autowired
    public PaymentRequestController(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
    }

    /**
     * Process and create payment request
     *
     * @param paymentRequest payment request body
     * @return id of created payment request
     */
    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public Long createPaymentRequest(@Valid @RequestBody PaymentRequestDTO paymentRequest) {
        return paymentRequestService.createPaymentRequest(PaymentRequest.builder()
                .routeId(paymentRequest.getRouteId())
                .clientId(paymentRequest.getClientId())
                .departureDateTime(ZonedDateTime.parse(paymentRequest.getDepartureDateTime(), DateTimeFormatter.ISO_DATE_TIME))
                .build());
    }

    /**
     * Get the payment request status by provided ID
     *
     * @param id payment request ID
     * @return status of payment request
     */
    @GetMapping("/{id}/status")
    @ResponseBody
    public PaymentRequestStatusDTO getPaymentReqestStatus(@PathVariable("id") Long id) {
        return PaymentRequestStatusDTO.valueOf(paymentRequestService.getPaymentRequestStatus(id).name());
    }

    /**
     * Get payment requests by client ID
     *
     * @param id client ID
     * @return list of payment requests
     */
    @GetMapping
    @ResponseBody
    public List<PaymentRequestDTO> getPaymentRequestsByClientId(@RequestParam("clientId") Long id) {
        return paymentRequestService.getPaymentRequestsByClientId(id)
                .stream()
                .map(pr -> PaymentRequestDTO.builder()
                        .clientId(pr.getClientId())
                        .id(pr.getId())
                        .routeId(pr.getRouteId())
                        .departureDateTime(pr.getDepartureDateTime().toOffsetDateTime().toString())
                        .requestStatus(PaymentRequestStatusDTO.valueOf(pr.getRequestStatus().name())).build())
                .collect(Collectors.toList());
    }

}
