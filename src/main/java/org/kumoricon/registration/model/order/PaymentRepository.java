package org.kumoricon.registration.model.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    @Query(value = "select sum(amount) from payments where session_id = ?1", nativeQuery = true)
    BigDecimal getTotalForSessionId(Integer id);

    @Query(value = "select sum(amount) from payments WHERE session_id = ?1 AND payment_type = ?2", nativeQuery=true)
    BigDecimal getTotalByPaymentTypeForSessionId(Integer id, Integer paymentType);

    List<Payment> findByTillSessionIdAndPaymentType(Integer tillSessionId, Payment.PaymentType paymentType);

    @Transactional
    @Modifying
    @Query(value = "delete from Payment p where p.id = ?1")
    void deleteById(Integer id);

}