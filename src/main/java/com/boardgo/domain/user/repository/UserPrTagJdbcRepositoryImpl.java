package com.boardgo.domain.user.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPrTagJdbcRepositoryImpl implements UserPrTagJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void bulkInsertPrTags(List<String> prTags, Long userInfoId) {
        if (prTags == null) {
            return;
        }
        String sql = "INSERT INTO user_pr_tag (tag_name, user_info_id) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, prTags.get(i));
                        ps.setLong(2, userInfoId);
                    }

                    @Override
                    public int getBatchSize() {
                        return prTags.size();
                    }
                });
    }
}
