package com.innowise.internship.paymentservice.mapper;

import com.innowise.internship.paymentservice.exception.InvalidPaymentStatusException;
import com.innowise.internship.paymentservice.model.PaymentStatus;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    List<PaymentStatus> toStatusList(List<String> statuses);

    default PaymentStatus stringToStatus(String status) {
        if (status == null) {
            return null;
        }
        try {
            return PaymentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidPaymentStatusException("Invalid payment status provided: " + status);
        }
    }
}
