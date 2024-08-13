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
public class GameGenreMatchJdbcRepositoryImpl implements GameGenreMatchJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void bulkInsert(Long boardGameId, List<Long> genreIdList) {

        String sql =
                "INSERT INTO game_genre_match (board_game_id, board_game_genre_id) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, boardGameId);
                        ps.setLong(2, genreIdList.get(i));
                    }

                    @Override
                    public int getBatchSize() {
                        return genreIdList.size();
                    }
                });
    }
}
