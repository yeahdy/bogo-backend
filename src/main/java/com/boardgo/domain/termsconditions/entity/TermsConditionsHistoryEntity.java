package com.boardgo.domain.termsconditions.entity;

import com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Getter
@Table(name = "terms_conditions_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TermsConditionsHistoryEntity {

    @Id
    @Column(name = "terms_conditions_history_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("약관동의 타입")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20, nullable = false)
    private TermsConditionsType type;

    @Comment("이용약관 제목")
    @Column(name = "title", nullable = false)
    private String title;

    @Comment("이용약관 내용")
    @Column(name = "content", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    @Comment("버전")
    @Column(name = "version", nullable = false, columnDefinition = "SMALLINT")
    private Integer version;

    @CreatedDate
    @Column(
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP",
            nullable = false,
            updatable = false)
    private LocalDateTime createdAt;

    public TermsConditionsHistoryEntity(
            TermsConditionsType type, String title, String content, Integer version) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.version = version;
    }
}
