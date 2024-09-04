package com.boardgo.domain.meeting.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

@Entity
@Getter
@Immutable
@Subselect(
        """
                	SELECT m.meeting_id AS id, COUNT(mp.meeting_participant_id) AS participant_count
                	FROM meeting m
                	INNER JOIN meeting_participant mp ON m.meeting_id = mp.meeting_id
                	WHERE mp.type != 'OUT'
                	GROUP BY m.meeting_id
                """)
@Synchronize({"meeting", "meeting_participant"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingParticipantSubEntity {
    @Id private Long id;

    @Column(name = "participantCount")
    private Integer participantCount;

    @Builder
    private MeetingParticipantSubEntity(Long id, Integer participantCount) {
        this.id = id;
        this.participantCount = participantCount;
    }

    public boolean isParticipated(Integer limitCount) {
        return this.participantCount < limitCount;
    }

    public boolean isAnyOneParticipated() {
        return this.participantCount > 1;
    }
}
