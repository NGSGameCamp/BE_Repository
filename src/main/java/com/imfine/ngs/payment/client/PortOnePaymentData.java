package com.imfine.ngs.payment.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortOnePaymentData {

    private String id; // 포트원 결제 ID (imp_uid)
    private String status;

    @JsonProperty("merchantId")
    private String merchantUid; // 가맹점 주문 ID

    private String orderName;
    private PortOneAmount amount;
    private Customer customer;
    private List<Cancellation> cancellations;
    private ZonedDateTime paidAt;
    private ZonedDateTime cancelledAt;


    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Customer {
        private String id;
        private String name;
        private String email;
        private String phoneNumber;
    }

    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Cancellation {
        private String status;
        private long totalAmount;
        private String reason;
        private ZonedDateTime cancelledAt;
    }
}
