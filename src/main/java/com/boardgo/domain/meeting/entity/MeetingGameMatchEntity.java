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
        name = "meeting_game_match",
        uniqueConstraints =
                @UniqueConstraint(
                        name = "meeting_game_match_unique",
                        columnNames = {"board_game_id", "meeting_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingGameMatchEntity extends BaseEntity {
    @Id
    @Column(name = "meeting_game_match_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "board_game_id")
    private Long boardGameId;

    @Column(name = "meeting_id")
    private Long meetingId;

    @Builder
    private MeetingGameMatchEntity(Long id, Long boardGameId, Long meetingId) {
        this.id = id;
        this.boardGameId = boardGameId;
        this.meetingId = meetingId;
    }
}
