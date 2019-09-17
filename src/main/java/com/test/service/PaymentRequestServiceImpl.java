package com.test.service;

import com.test.domain.PaymentRequest;
import com.test.domain.PaymentRequestStatus;
import com.test.exception.NoSuchPaymentRequestException;
import com.test.exception.RequestAlreadyExistException;
import com.test.repository.PaymentRequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Implementation of {@link PaymentRequestService}
 */
@Service
public class PaymentRequestServiceImpl implements PaymentRequestService {
    private final PaymentRequestRepo paymentRequestRepo;

    @Autowired
    public PaymentRequestServiceImpl(PaymentRequestRepo paymentRequestRepo) {
        this.paymentRequestRepo = paymentRequestRepo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long createPaymentRequest(PaymentRequest paymentRequest) {
        paymentRequest.setTicketId(Objects.hash(paymentRequest.getClientId(),
                paymentRequest.getRouteId(),
                paymentRequest.getDepartureDateTime()));
        if (paymentRequestRepo.getRequestByTicketId(paymentRequest.getTicketId()).isPresent()) {
            throw new RequestAlreadyExistException("Request Already Exist");
        } else {
            return paymentRequestRepo.save(paymentRequest).getId();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentRequestStatus getPaymentRequestStatus(Long id) {
        return paymentRequestRepo.findById(id)
                .orElseThrow(() -> new NoSuchPaymentRequestException("Payment request with id {" + id + "} not found!"))
                .getRequestStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PaymentRequest> getPaymentRequestsByClientId(Long clientId) {
        return StreamSupport.stream(paymentRequestRepo.findAll().spliterator(), false)
                .filter(paymentRequest -> paymentRequest.getClientId().equals(clientId)
                        && paymentRequest.getDepartureDateTime().isAfter(ZonedDateTime.now()))
                .sorted(Comparator.comparing(PaymentRequest::getDepartureDateTime))
                .collect(Collectors.toList());
    }
}
