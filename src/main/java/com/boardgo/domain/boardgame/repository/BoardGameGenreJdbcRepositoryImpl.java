package com.boardgo.domain.boardgame.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardGameGenreJdbcRepositoryImpl implements BoardGameGenreJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void bulkInsert(List<String> genres) {

        String sql = "INSERT IGNORE INTO board_game_genre (genre) VALUES (?)";

        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, genres.get(i));
                    }

                    @Override
                    public int getBatchSize() {
                        return genres.size();
                    }
                });
    }
}
