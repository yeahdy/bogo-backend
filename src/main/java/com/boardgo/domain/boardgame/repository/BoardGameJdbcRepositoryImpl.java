package com.boardgo.domain.boardgame.repository;

import com.boardgo.domain.boardgame.entity.BoardGameEntity;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardGameJdbcRepositoryImpl implements BoardGameJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void bulkInsert(List<BoardGameEntity> boardGameEntityList) {
        String sql =
                "INSERT INTO board_game (title, thumbnail, min_people, max_peole, min_playtime, max_playtime) VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, boardGameEntityList.get(i).getTitle());
                        ps.setString(2, boardGameEntityList.get(i).getThumbnail());
                        ps.setInt(3, boardGameEntityList.get(i).getMinPeople());
                        ps.setInt(4, boardGameEntityList.get(i).getMaxPeople());
                        ps.setInt(5, boardGameEntityList.get(i).getMinPlaytime());
                        ps.setInt(6, boardGameEntityList.get(i).getMaxPlaytime());
                    }

                    @Override
                    public int getBatchSize() {
                        return boardGameEntityList.size();
                    }
                });
    }
}
