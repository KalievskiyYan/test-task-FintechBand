package com.test.service;

import com.test.domain.PaymentRequestStatus;
import com.test.repository.PaymentRequestRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.stream.StreamSupport;

/**
 * Service execute payment request
 */
@Slf4j
@Service
public class PaymentRequestExecutionService {

    private final PaymentRequestRepo paymentRequestRepo;
    private final RestTemplate restTemplate;
    private final TaskExecutor taskExecutor;

    @Autowired
    public PaymentRequestExecutionService(PaymentRequestRepo paymentRequestRepo,
                                          RestTemplate restTemplate,
                                          @Qualifier("paymentRequestTaskExecutor") TaskExecutor taskExecutor) {
        this.paymentRequestRepo = paymentRequestRepo;
        this.restTemplate = restTemplate;
        this.taskExecutor = taskExecutor;
    }

    /**
     * Retrieves payment requests from repository and process all not final statuses using configured tasks executor.
     */
    @Scheduled(fixedRate = 60000)
    public void execute() {
        log.info("payment request execution started");
        StreamSupport.stream(paymentRequestRepo.findAll().spliterator(), false)
                .filter(request -> request.getRequestStatus() != PaymentRequestStatus.DONE
                        && request.getRequestStatus() != PaymentRequestStatus.ERROR)
                .forEach(paymentRequest ->
                        taskExecutor.execute(() -> {
                            log.debug(Thread.currentThread().getName());
                            paymentRequest.setRequestStatus(
                                    restTemplate.getForObject("http://localhost:7777/payment-status-generator",
                                            PaymentRequestStatus.class));
                            paymentRequestRepo.save(paymentRequest);
                        }));
    }
}
