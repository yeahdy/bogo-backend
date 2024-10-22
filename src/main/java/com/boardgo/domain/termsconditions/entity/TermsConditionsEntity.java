package com.boardgo.domain.termsconditions.entity;

import com.boardgo.common.converter.BooleanConverter;
import com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Table(
        name = "terms_conditions",
        uniqueConstraints = {
            @UniqueConstraint(name = "terms_conditions_type_unique", columnNames = "type")
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TermsConditionsEntity {

    @Id
    @Column(name = "terms_conditions_id")
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
    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

    @Comment("필수 여부(Y/N)")
    @Convert(converter = BooleanConverter.class)
    @Column(name = "required", nullable = false, columnDefinition = "char(1)")
    private Boolean required;

    @Builder
    private TermsConditionsEntity(
            TermsConditionsType type, String title, String content, boolean required) {
        this.type = type;
        this.title = title;
        this.content = content;
        this.required = required;
    }

    public boolean isRequired(Boolean required) {
        if (this.getRequired()) {
            return required;
        }
        return true;
    }
}
