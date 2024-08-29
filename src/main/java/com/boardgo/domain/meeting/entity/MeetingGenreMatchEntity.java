package com.boardgo.domain.meeting.entity;

import com.boardgo.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "meeting_genre_match",
        uniqueConstraints =
                @UniqueConstraint(
                        name = "meeting_genre_match_unique",
                        columnNames = {"board_game_genre_id", "meeting_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingGenreMatchEntity extends BaseEntity {
    @Id
    @Column(name = "meeting_genre_match_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meeting_id")
    private Long meetingId;

    @Column(name = "board_game_genre_id")
    private Long boardGameGenreId;

    @Builder
    private MeetingGenreMatchEntity(Long id, Long meetingId, Long boardGameGenreId) {
        this.id = id;
        this.meetingId = meetingId;
        this.boardGameGenreId = boardGameGenreId;
    }
}
