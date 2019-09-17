package com.test.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDTO {
    @JsonProperty("id")
    Long id;

    @NotNull(message = "Client id can`t be null")
    @JsonProperty("clientId")
    Long clientId;

    @NotNull(message = "Route id can`t be null")
    @JsonProperty("routeId")
    Long routeId;

    @NotNull(message = "Departure Date Time can`t be null")
    @JsonProperty("departureDateTime")
    String departureDateTime;

    @JsonProperty("requestStatus")
    PaymentRequestStatusDTO requestStatus;
}
