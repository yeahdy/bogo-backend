package com.boardgo.domain.termsconditions.entity;

import com.boardgo.common.converter.BooleanConverter;
import com.boardgo.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Table(name = "user_terms_conditions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserTermsConditionsEntity extends BaseEntity {

    @Id
    @Column(name = "user_terms_conditions_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("회원 고유 id")
    @Column(name = "user_info_id", nullable = false)
    private Long userInfoId;

    @Comment("약관동의 타입")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "terms_conditions_id")
    private TermsConditionsEntity termsConditions;

    @Comment("동의여부(Y/N)")
    @Convert(converter = BooleanConverter.class)
    @Column(name = "agreement", nullable = false, columnDefinition = "char(1)")
    private Boolean agreement;

    @Builder
    private UserTermsConditionsEntity(
            Long userInfoId, TermsConditionsEntity termsConditionsEntity, boolean agreement) {
        this.userInfoId = userInfoId;
        this.termsConditions = termsConditionsEntity;
        this.agreement = agreement;
    }
}
