package org.kumoricon.registration.model.loginsession;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

/**
 * I found it easier to just query the database for active sessions instead of trying to read from the Spring
 * Session objects. This also means that it would show all sessions, not just sessions ONLY on this server, which
 * means the list will be more consistent.
 */
@Repository
public class LoginRepository {
    private final JdbcTemplate jdbcTemplate;

    public LoginRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<SessionInfoDTO> findAll() {
        try {
            return jdbcTemplate.query(
                    "select " +
                            "       s.PRIMARY_ID as \"primaryId\", " +
                            "       s.PRINCIPAL_NAME as \"principalName\"," +
                            "       s.CREATION_TIME as \"creationTime\"," +
                            "       s.LAST_ACCESS_TIME as \"lastAccessTime\", " +
                            "       s.EXPIRY_TIME as \"expiryTime\" " +
                            "from SPRING_SESSION s where s.principal_name is not null",
                    new SessionInfoDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }


    @Transactional
    public void updateLoginRecords() {
        // This query will select sessions from the spring_session table with a non-null username (logged in users)
        // and then insert a record in to the loginsessions table with that user's id and the current timestamp rounded
        // down to 15 minute intervals (IE, 1:00, 1:15, 1:30, 1:45, 2:00...) as long as the session expires after the
        // current time.
        final String sql = "insert into loginsessions (users_id, start) " +
                "select idexp.id, " +
                "date_trunc('hour', now() at time zone 'utc') + date_part('minute', now() at time zone 'utc')::int / 15 * interval '15 min' as bucket " +
                "from (" +
                "select users.id," +
                "to_timestamp(spring_session.expiry_time/1000) at time zone 'utc' as expiration "+
                "from users join spring_session on spring_session.principal_name=users.username " +
                "where spring_session.principal_name is not null) " +
                "as idexp " +
                "WHERE idexp.expiration > now() at time zone 'utc' " +
                "on conflict do nothing;";
        jdbcTemplate.update(sql);
    }

    @Transactional
    public void deleteLoginSessionsForUsername(String username) {
        final String sql = "DELETE FROM spring_session WHERE principal_name = ?";
        jdbcTemplate.update(sql, new Object[] {username});
    }

    @Transactional
    public void deleteLoginSessionsForUserId(Integer userId) {
        final String sql = "DELETE FROM spring_session WHERE principal_name = (select username from users where id = ?)";
        jdbcTemplate.update(sql, new Object[] {userId});
    }


    @Transactional(readOnly = true)
    public List<LocalDate> getAvailableDays() {
        final String sql = "select distinct date(start at time zone 'PST') as start from loginsessions order by start desc limit 5";
        return jdbcTemplate.query(sql, new LocalDateRowMapper());
    }

    @Transactional(readOnly = true)
    public List<LoginTimePeriod> getLoginTimePeriods(OffsetDateTime startTime, OffsetDateTime endTime) {
        final String sql = "select loginsessions.start, u.first_name, u.last_name from loginsessions join users u on loginsessions.users_id = u.id where start >= ? and start <= ? order by u.first_name, u.last_name;";
        return jdbcTemplate.query(sql, new Object[]{startTime, endTime}, new LoginTimePeriodRowMapper());
    }

    private class LocalDateRowMapper implements RowMapper<LocalDate> {
        @Override
        public LocalDate mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getDate("start").toLocalDate();
        }
    }

    private class SessionInfoDTORowMapper implements RowMapper<SessionInfoDTO> {
        @Override
        public SessionInfoDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            SessionInfoDTO s = new SessionInfoDTO(rs.getString("primaryId"),
                    rs.getString("principalName"),
                    rs.getLong("creationTime"),
                    rs.getLong("lastAccessTime"),
                    rs.getLong("expiryTime"));
            return s;
        }
    }

    private class LoginTimePeriodRowMapper implements RowMapper<LoginTimePeriod> {
        @Override
        public LoginTimePeriod mapRow(ResultSet rs, int rowNum) throws SQLException {
            Timestamp ts = rs.getTimestamp("start");
            Instant i = ts == null ? null : ts.toInstant();
            LoginTimePeriod l = new LoginTimePeriod(i, rs.getString("first_name") + " " + rs.getString("last_name"));
            
            return l;
        }
    }
}
