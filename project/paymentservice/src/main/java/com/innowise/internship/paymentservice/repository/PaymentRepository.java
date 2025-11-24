package com.innowise.internship.paymentservice.repository;

import com.innowise.internship.paymentservice.model.Payment;
import com.innowise.internship.paymentservice.model.PaymentStatus;
import org.bson.types.Decimal128;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    List<Payment> findByOrderId(Long orderId);

    List<Payment> findByUserId(Long userId);

    List<Payment> findByPaymentStatusIn(Collection<PaymentStatus> statuses);

    @Aggregation(
            pipeline = {
                    "{ $match:  { 'timestamp' : { $gte: ?0, $lte: ?1 }}}",
                    "{ $group: { _id: null, totalAmount: { $sum: '$payment_amount' } } }"
            })
    Decimal128 getPaymentSumByDateRange(Instant startDate, Instant endDate);
}
