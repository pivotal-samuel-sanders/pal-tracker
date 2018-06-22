package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<TimeEntry> rowMapper = ((rs, rowNum) -> {
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setId(rs.getLong("id"));
        timeEntry.setProjectId(rs.getLong("project_id"));
        timeEntry.setUserId(rs.getLong("user_id"));
        timeEntry.setDate(rs.getDate("date").toLocalDate());
        timeEntry.setHours(rs.getInt("hours"));

        return timeEntry;
    });
    private final ResultSetExtractor<TimeEntry> extractor =
            rs -> rs.next() ? rowMapper.mapRow(rs, 1) : null;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql =
                "insert into time_entries " +
                        "(project_id, user_id, date, hours)" +
                        "values (?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, RETURN_GENERATED_KEYS);
            statement.setLong(1, timeEntry.getProjectId());
            statement.setLong(2, timeEntry.getUserId());
            statement.setDate(3, Date.valueOf(timeEntry.getDate()));
            statement.setInt(4, timeEntry.getHours());
            return statement;
        }, keyHolder);

        long id = keyHolder.getKey().longValue();
        timeEntry.setId(id);

        return timeEntry;
    }

    @Override
    public TimeEntry find(long id) {
        String sql = "select id, project_id, user_id, date, hours from time_entries where id = ?";

        TimeEntry timeEntry = jdbcTemplate.query(sql, new Object[]{id}, extractor);

        return timeEntry;
    }

    @Override
    public List<TimeEntry> list() {
        String sql = "select id, project_id, user_id, date, hours from time_entries";

        List<TimeEntry> timeEntries = jdbcTemplate.query(sql, rowMapper);

        return timeEntries;
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {

        String sql = "update time_entries set project_id = ?, user_id = ?, date = ?, hours = ? where id = ?";
        jdbcTemplate.update(sql,
                timeEntry.getProjectId(),
                timeEntry.getUserId(),
                Date.valueOf(timeEntry.getDate()),
                timeEntry.getHours(),
                id);

        timeEntry.setId(id);

        return timeEntry;
    }

    @Override
    public TimeEntry delete(long id) {
        String sql = "delete from time_entries where id = ?";

        jdbcTemplate.update(sql, id);

        return null;
    }

}
