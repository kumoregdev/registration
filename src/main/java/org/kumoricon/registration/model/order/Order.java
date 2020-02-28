package org.kumoricon.registration.model.order;

import org.kumoricon.registration.model.user.User;

import java.util.*;

/**
 * Represents a single order. It is assumed that either all attendees in an order have not paid,
 * or all attendees in an order have paid - no support for partial orders. This shouldn't come up
 * during regular usage, but could with imported data.
 */
public class Order {
    private Integer id;
    private String orderId;
    private Boolean paid;

    private Integer orderTakenByUser;
    private String notes;

    public Order() {
        this.paid = false;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }


    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public Boolean getPaid() { return paid; }
    public void setPaid(Boolean paid) { this.paid = paid; }


    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getOrderTakenByUser() { return orderTakenByUser; }
    public void setOrderTakenByUser(User orderTakenByUser) { this.orderTakenByUser = orderTakenByUser.getId(); }
    public void setOrderTakenByUser(Integer userId) { this.orderTakenByUser = userId; }

    public static String generateOrderId() {
        String symbols = "abcdefghijklmnopqrstuvwxyz01234567890";
        Random random = new Random();
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            output.append(symbols.charAt(random.nextInt(symbols.length())));
        }
        return output.toString();
    }

    public String toString() {
        if (id != null) {
            return String.format("[Order %s: %s]", id, orderId);
        } else {
            return String.format("[Order: %s]", orderId);
        }
    }
}
