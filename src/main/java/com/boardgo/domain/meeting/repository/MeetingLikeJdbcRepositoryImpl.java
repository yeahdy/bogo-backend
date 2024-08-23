package com.boardgo.domain.meeting.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MeetingLikeJdbcRepositoryImpl implements MeetingLikeJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void bulkInsert(List<Long> meetingIdList, Long userId) {
        String sql = "INSERT IGNORE INTO meeting_like (user_info_id, meeting_id) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, userId);
                        ps.setLong(2, meetingIdList.get(i));
                    }

                    @Override
                    public int getBatchSize() {
                        return meetingIdList.size();
                    }
                });
    }
}
