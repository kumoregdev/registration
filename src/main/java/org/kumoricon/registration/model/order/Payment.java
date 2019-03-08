package org.kumoricon.registration.model.order;


import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.kumoricon.registration.model.Record;
import org.kumoricon.registration.model.tillsession.TillSession;
import org.kumoricon.registration.model.user.User;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name="payments")
public class Payment extends Record {
    @NotNull
    @Min(0)
    private BigDecimal amount;

    @NotNull
    @ManyToOne
    private Order order;

    private Integer paymentTakenBy;

    @NotNull
    private PaymentType paymentType;
    private Instant paymentTakenAt;
    private String paymentLocation;
    private String authNumber;

    @NotNull
    @ManyToOne
    private TillSession session;

    public enum PaymentType {
        CASH(0) {
            public String toString() {
                return "Cash";
            }
        },
        CHECK(1) {
            public String toString() {
                return "Check/Money Order";
            }
        },
        CREDIT(2) {
            public String toString() {
                return "Credit Card";
            }
        },
        PREREG(3) {
            public String toString() {
                return "Pre Reg";
            }
        };

        private final int value;
        PaymentType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static PaymentType fromInteger(Integer typeId) {
            PaymentType[] paymentTypes = PaymentType.values();
            return paymentTypes[typeId];
        }
    }

    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Order getOrder() {
        return order;
    }
    public void setOrder(Order order) {
        this.order = order;
    }

    public Integer getPaymentTakenBy() {
        return paymentTakenBy;
    }
    public void setPaymentTakenBy(User paymentTakenBy) {
        this.paymentTakenBy = paymentTakenBy.getId();
    }
    public void setPaymentTakenBy(Integer userId) {
        this.paymentTakenBy = userId;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }
    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public Instant getPaymentTakenAt() {
        return paymentTakenAt;
    }
    public void setPaymentTakenAt(Instant paymentTakenAt) {
        this.paymentTakenAt = paymentTakenAt;
    }

    public String getPaymentLocation() {
        return paymentLocation;
    }
    public void setPaymentLocation(String paymentLocation) {
        this.paymentLocation = paymentLocation;
    }

    public String getAuthNumber() { return authNumber; }
    public void setAuthNumber(String authNumber) { this.authNumber = authNumber; }

    public TillSession getSession() { return session; }
    public void setSession(TillSession session) { this.session = session; }

    public String toString() {
        if (id != null) {
            return String.format("[Payment %s: $%s]", id, getAmount());
        } else {
            return String.format("[Payment: $%s]", getAmount());
        }
    }
}