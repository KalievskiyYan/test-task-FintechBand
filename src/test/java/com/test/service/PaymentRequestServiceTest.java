package com.test.service;

import com.test.domain.PaymentRequest;
import com.test.domain.PaymentRequestStatus;
import com.test.exception.NoSuchPaymentRequestException;
import com.test.exception.RequestAlreadyExistException;
import com.test.repository.PaymentRequestRepo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class PaymentRequestServiceTest {

    @InjectMocks
    private PaymentRequestServiceImpl paymentRequestService;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();


    @Mock
    private PaymentRequestRepo paymentRequestRepo;

    @Test
    public void shouldCreatePaymentRequest() {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .clientId(2L)
                .routeId(3L)
                .requestStatus(PaymentRequestStatus.PROCESSING)
                .departureDateTime(ZonedDateTime.now())
                .build();
        when(paymentRequestRepo.save(eq(paymentRequest))).thenReturn(
                PaymentRequest.builder()
                        .id(1L)
                        .clientId(paymentRequest.getClientId())
                        .routeId(paymentRequest.getRouteId())
                        .requestStatus(paymentRequest.getRequestStatus())
                        .departureDateTime(paymentRequest.getDepartureDateTime())
                        .build());
        assertEquals(paymentRequestService.createPaymentRequest(paymentRequest), Long.valueOf(1L));
        verify(paymentRequestRepo, times(1)).save(any(PaymentRequest.class));

    }

    @Test
    public void shouldThrowRequestAlreadyExistException() {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .id(1L)
                .clientId(2L)
                .routeId(3L)
                .requestStatus(PaymentRequestStatus.PROCESSING)
                .departureDateTime(ZonedDateTime.now())
                .build();
        paymentRequest.setTicketId(Objects.hash(paymentRequest.getClientId(),
                paymentRequest.getRouteId(),
                paymentRequest.getDepartureDateTime()));
        when(paymentRequestRepo.getRequestByTicketId(eq(paymentRequest.getTicketId()))).thenReturn(Optional.of(paymentRequest));
        expectedEx.expect(RequestAlreadyExistException.class);
        expectedEx.expectMessage("Request Already Exist");
        paymentRequestService.createPaymentRequest(paymentRequest);
    }

    @Test
    public void shouldReturnPaymentRequestStatus() {
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .id(1L)
                .clientId(2L)
                .routeId(3L)
                .requestStatus(PaymentRequestStatus.PROCESSING)
                .departureDateTime(ZonedDateTime.now())
                .build();
        when(paymentRequestRepo.findById(eq(1L))).thenReturn(Optional.of(paymentRequest));
        assertThat(paymentRequestService.getPaymentRequestStatus(1L), is(PaymentRequestStatus.PROCESSING));
    }

    @Test
    public void shouldThrowNoSuchPaymentRequestException() {
        when(paymentRequestRepo.findById(eq(1L))).thenReturn(Optional.empty());
        expectedEx.expectMessage("Payment request with id {1} not found!");
        expectedEx.expect(NoSuchPaymentRequestException.class);
        paymentRequestService.getPaymentRequestStatus(1L);
    }

    @Test
    public void shouldGetPaymentRequestsByClientId() {
        List<PaymentRequest> paymentRequests = new ArrayList<PaymentRequest>();
        for (int i = 0; i < 10; i++) {
            paymentRequests.add(
                    PaymentRequest.builder()
                            .id((long) i)
                            .clientId(2L)
                            .routeId(i * 2L)
                            .requestStatus(PaymentRequestStatus.ERROR)
                            .departureDateTime(ZonedDateTime.now().minusWeeks(5).plusWeeks(i))
                            .build()
            );
        }
        when(paymentRequestRepo.findAll()).thenReturn(paymentRequests);
        List<PaymentRequest> paymentRequestsFuture = paymentRequests.stream()
                .filter(paymentRequest -> paymentRequest.getDepartureDateTime().isAfter(ZonedDateTime.now()))
                .collect(Collectors.toList());
        assertEquals(paymentRequestService.getPaymentRequestsByClientId(2L), paymentRequestsFuture);
    }

    @Test
    public void shouldReturnEmptyListOfPaymentRequestsByClientId(){
        List<PaymentRequest> listPaymentRequest = new ArrayList<PaymentRequest>();
        for (int i = 0; i < 10; i++) {
            listPaymentRequest.add(
                    PaymentRequest.builder()
                            .id((long) i)
                            .clientId(2L)
                            .routeId(i * 2L)
                            .requestStatus(PaymentRequestStatus.ERROR)
                            .departureDateTime(ZonedDateTime.now().minusWeeks(i))
                            .build()
            );
        }
        when(paymentRequestRepo.findAll()).thenReturn(listPaymentRequest);
        assertEquals(paymentRequestService.getPaymentRequestsByClientId(2L), Collections.emptyList());
    }
}