package com.boardgo.domain.review.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "evaluation_tag")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EvaluationTagEntity {

    @Id
    @Column(name = "evaluation_tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "evaluation_type", length = 20, nullable = false)
    private EvaluationType evaluationType;

    @Column(name = "tag_phrase", nullable = false)
    private String tagPhrase;
}
