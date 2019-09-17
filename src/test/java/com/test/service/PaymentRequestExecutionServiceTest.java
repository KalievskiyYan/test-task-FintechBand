package com.test.service;

import com.test.domain.PaymentRequest;
import com.test.domain.PaymentRequestStatus;
import com.test.repository.PaymentRequestRepo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaymentRequestExecutionServiceTest {

    @Mock
    private PaymentRequestRepo paymentRequestRepo;
    @Mock
    private RestTemplate restTemplate;
    @Spy
    private ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

    @InjectMocks
    private PaymentRequestExecutionService paymentRequestExecutionService;

    @Test
    public void execute() {
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(5);
        taskExecutor.initialize();
        List<PaymentRequest> paymentRequestList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            paymentRequestList.add(PaymentRequest.builder()
                    .clientId((long) i * 10L)
                    .routeId((long) i * 20L)
                    .departureDateTime(ZonedDateTime.now().plusDays(i).minusMonths(i))
                    .id((long) i)
                    .requestStatus(i % 2 == 0 ? PaymentRequestStatus.DONE : PaymentRequestStatus.PROCESSING)
                    .build());
        }
        when(paymentRequestRepo.findAll()).thenReturn(paymentRequestList);
        paymentRequestExecutionService.execute();
        verify(restTemplate, times(25)).getForObject(eq("http://localhost:7777/payment-status-generator"),
                eq(PaymentRequestStatus.class));
        verify(paymentRequestRepo, times(25)).save(any(PaymentRequest.class));

    }
}