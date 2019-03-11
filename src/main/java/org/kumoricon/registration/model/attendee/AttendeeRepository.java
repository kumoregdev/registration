package org.kumoricon.registration.model.attendee;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.kumoricon.registration.model.SqlHelper.translate;

@Repository
public class AttendeeRepository {
    private final JdbcTemplate jdbcTemplate;

    public AttendeeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly=true)
    public List<Attendee> findByOrderNumber(String orderNumber) {
        try {
            return jdbcTemplate.query(
                    "select * from attendees join orders on attendee.order_id = orders.id where orders.order_id = ? order by timestamp desc",
                    new Object[]{orderNumber}, new AttendeeRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    @Transactional(readOnly=true)
    public List<Attendee> findAllByOrderId(int id) {
        try {
            return jdbcTemplate.query(
                    "select * from attendees where order_id = ? order by id desc",
                    new Object[]{id}, new AttendeeRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

//    @Query(value = "SELECT DATE(check_in_time at time zone 'America/Los_Angeles') as CheckInDate, COUNT(id) AS cnt, SUM(paid_amount) as Amount FROM attendees WHERE checked_in = TRUE AND pre_registered = FALSE GROUP BY CheckInDate ORDER BY CheckInDate;", nativeQuery = true)
//    List<Object[]> findAtConCheckInCountsByDate();
//
//    @Query(value = "SELECT DATE(check_in_time at time zone 'America/Los_Angeles') as CheckInDate, COUNT(id) AS cnt, SUM(paid_amount) as Amount FROM attendees WHERE checked_in = TRUE AND pre_registered = TRUE GROUP BY CheckInDate ORDER BY CheckInDate;", nativeQuery = true)
//    List<Object[]> findPreRegCheckInCountsByDate();
//
//    @Query(value = "SELECT DATE(check_in_time at time zone 'America/Los_Angeles') as checkInDate, EXTRACT(HOUR from check_in_time at time zone 'America/Los_Angeles') as checkInHour, COALESCE(atConCheckedIn.cnt, 0) as AtConCheckedIn, COALESCE(preRegCheckedIn.cnt, 0) as PreRegCheckedIn, COUNT(checked_in) as Total FROM attendees LEFT OUTER JOIN (SELECT DATE(check_in_time at time zone 'America/Los_Angeles') as aCheckInDate, EXTRACT(HOUR from check_in_time at time zone 'America/Los_Angeles') as aCheckInHour, COUNT(attendees.checked_in) as cnt FROM attendees  WHERE attendees.checked_in = TRUE AND attendees.pre_registered = TRUE GROUP BY aCheckInDate, aCheckInHour) as preRegCheckedIn ON DATE(check_in_time at time zone 'America/Los_Angeles') = preRegCheckedIn.aCheckInDate AND EXTRACT(HOUR from check_in_time at time zone 'America/Los_Angeles') = preRegCheckedIn.aCheckInHour LEFT OUTER JOIN (SELECT DATE(check_in_time at time zone 'America/Los_Angeles') as aCheckInDate, EXTRACT(HOUR from check_in_time at time zone 'America/Los_Angeles') as aCheckInHour, COUNT(attendees.checked_in) as cnt FROM attendees  WHERE attendees.checked_in = TRUE AND attendees.pre_registered = FALSE GROUP BY aCheckInDate, aCheckInHour) as atConCheckedIn ON DATE(check_in_time at time zone 'America/Los_Angeles') = atConCheckedIn.aCheckInDate AND EXTRACT(HOUR from check_in_time at time zone 'America/Los_Angeles') = atConCheckedIn.aCheckInHour WHERE checked_in = TRUE GROUP BY checkInDate, checkInHour, atConCheckedIn.cnt, preRegCheckedIn.cnt ORDER BY checkInDate DESC, checkInHour;", nativeQuery = true)
//    List<Object[]> findCheckInCountsByHour();

    @Transactional(readOnly=true)
    public Integer findWarmBodyCount() {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM (SELECT DISTINCT first_name, last_name, zip, birth_date FROM attendees WHERE checked_in=TRUE) as t",
                    Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    // Total attendee count calculation. From: https://www.kumoricon.org/history
    // Attendance figures for all years are unique, paid (rather than "turnstile")—this means that weekend badges are
    // counted only once, and the count is a number of unique individual attendees who registered in a given year.
    // Attendance figures count paid membership purchases at standard or VIP rates (staff, exhibitors, artists,
    // guests, industry, press, and complimentary badges are not counted). Prior to 2014, multiple single-day badges
    // were double-counted (for example, a person purchases Saturday, then Sunday the next day); for 2014 and after,
    // only one is counted (this is an estimated less than 2% discrepancy).
    @Transactional(readOnly=true)
    public Integer findTotalAttendeeCount() {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM (SELECT DISTINCT first_name, last_name, zip, birth_date FROM attendees WHERE checked_in=TRUE AND paid_amount > 0) as t",
                    Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    @Transactional(readOnly = true)
    public Integer count() {
        String sql = "select count(*) from attendees";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Transactional(readOnly=true)
    public Attendee findByOrderId(int orderId) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from attendees where order_id=?",
                    new Object[]{orderId}, new AttendeeRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    @Transactional(readOnly=true)
    public Attendee findById(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from attendees where id=?",
                    new Object[]{id}, new AttendeeRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Transactional
    public void delete(Integer attendeeId) {
        if (attendeeId != null) {
            jdbcTemplate.update("DELETE FROM attendees WHERE id = ?", attendeeId);
        }
    }

    @Transactional
    public void save(Attendee attendee) {
        if (attendee.getId() == null) {
            jdbcTemplate.update("INSERT INTO attendees(badge_id, badge_number, badge_pre_printed, badge_printed, birth_date, check_in_time, checked_in, comped_badge, country, email, emergency_contact_full_name, emergency_contact_phone, fan_name, first_name, last_name, legal_first_name, legal_last_name, name_is_legal_name, paid, paid_amount, parent_form_received, parent_full_name, parent_is_emergency_contact, parent_phone, phone_number, pre_registered, zip, order_id) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                    attendee.getBadgeId(), attendee.getBadgeNumber(), attendee.isBadgePrePrinted(),
                    attendee.isBadgePrinted(), attendee.getBirthDate(), translate(attendee.getCheckInTime()),
                    attendee.getCheckedIn(), attendee.getCompedBadge(), attendee.getCountry(), attendee.getEmail(),
                    attendee.getEmergencyContactFullName(), attendee.getEmergencyContactPhone(), attendee.getFanName(),
                    attendee.getFirstName(), attendee.getLastName(), attendee.getLegalFirstName(),
                    attendee.getLegalLastName(), attendee.getNameIsLegalName(), attendee.getPaid(),
                    attendee.getPaidAmount(), attendee.getParentFormReceived(), attendee.getParentFullName(),
                    attendee.getParentIsEmergencyContact(), attendee.getParentPhone(), attendee.getPhoneNumber(),
                    attendee.isPreRegistered(), attendee.getZip(), attendee.getOrderId());
        } else {
            jdbcTemplate.update("UPDATE attendees SET badge_id = ?, badge_number = ?, badge_pre_printed = ?, badge_printed = ?, birth_date = ?, check_in_time = ?, checked_in=?, comped_badge=?, country=?, email=?, emergency_contact_full_name=?, emergency_contact_phone=?, fan_name=?, first_name=?, last_name=?, legal_first_name=?, legal_last_name=?, name_is_legal_name=?, paid=?, paid_amount=?, parent_form_received=?, parent_full_name=?, parent_is_emergency_contact=?, parent_phone=?, phone_number=?, pre_registered=?, zip=?, order_id=? WHERE id = ?",
                    attendee.getBadgeId(), attendee.getBadgeNumber(), attendee.isBadgePrePrinted(),
                    attendee.isBadgePrinted(), attendee.getBirthDate(), translate(attendee.getCheckInTime()),
                    attendee.getCheckedIn(), attendee.getCompedBadge(), attendee.getCountry(), attendee.getEmail(),
                    attendee.getEmergencyContactFullName(), attendee.getEmergencyContactPhone(), attendee.getFanName(),
                    attendee.getFirstName(), attendee.getLastName(), attendee.getLegalFirstName(),
                    attendee.getLegalLastName(), attendee.getNameIsLegalName(), attendee.getPaid(),
                    attendee.getPaidAmount(), attendee.getParentFormReceived(), attendee.getParentFullName(),
                    attendee.getParentIsEmergencyContact(), attendee.getParentPhone(), attendee.getPhoneNumber(),
                    attendee.isPreRegistered(), attendee.getZip(), attendee.getOrderId(), attendee.getId());
        }
    }

    @Transactional(readOnly=true)
    public List<Attendee> findAll() {
        try {
            return jdbcTemplate.query(
                    "select * from attendees", new AttendeeRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    class AttendeeRowMapper implements RowMapper<Attendee>
    {
        @Override
        public Attendee mapRow(ResultSet rs, int rowNum) throws SQLException {
            Attendee a = new Attendee();

            a.setId(rs.getInt("id"));
            a.setBadgeId(rs.getInt("badge_id"));
            a.setBadgeNumber(rs.getString("badge_number"));
            a.setBadgePrePrinted(rs.getBoolean("badge_pre_printed"));
            a.setBadgePrinted(rs.getBoolean("badge_printed"));
            Date birthDate = rs.getDate("birth_date");
            if (birthDate != null) {
                a.setBirthDate(birthDate.toLocalDate());
            } else {
                a.setBirthDate(null);
            }
            Timestamp checkInTime = rs.getTimestamp("check_in_time");
            if (checkInTime != null) {
                a.setCheckInTime(checkInTime.toInstant());
            } else {
                a.setCheckInTime(null);
            }

            a.setCheckedIn(rs.getBoolean("checked_in"));
            a.setCompedBadge(rs.getBoolean("comped_badge"));
            a.setCountry(rs.getString("country"));
            a.setEmail(rs.getString("email"));
            a.setEmergencyContactFullName(rs.getString("emergency_contact_full_name"));
            a.setEmergencyContactPhone(rs.getString("emergency_contact_phone"));
            a.setFanName(rs.getString("fan_name"));
            a.setFirstName(rs.getString("first_name"));
            a.setLastName(rs.getString("last_name"));
            a.setLegalFirstName(rs.getString("legal_first_name"));
            a.setLegalLastName(rs.getString("legal_last_name"));
            a.setNameIsLegalName(rs.getBoolean("name_is_legal_name"));
            a.setPaid(rs.getBoolean("paid"));
            a.setPaidAmount(rs.getBigDecimal("paid_amount"));
            a.setParentFormReceived(rs.getBoolean("parent_form_received"));
            a.setParentFullName(rs.getString("parent_full_name"));
            a.setParentIsEmergencyContact(rs.getBoolean("parent_is_emergency_contact"));
            a.setParentPhone(rs.getString("parent_phone"));
            a.setPhoneNumber(rs.getString("phone_number"));
            a.setPreRegistered(rs.getBoolean("pre_registered"));
            a.setZip(rs.getString("zip"));
            a.setOrderId(rs.getInt("order_id"));
            return a;
        }
    }

}