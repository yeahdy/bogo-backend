package com.boardgo.domain.mapper;

import com.boardgo.domain.termsconditions.entity.TermsConditionsEntity;
import com.boardgo.domain.termsconditions.entity.UserTermsConditionsEntity;
import com.boardgo.domain.termsconditions.service.response.TermsConditionsResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TermsConditionsMapper {
    TermsConditionsMapper INSTANCE = Mappers.getMapper(TermsConditionsMapper.class);

    UserTermsConditionsEntity toUserTermsConditionsEntity(
            TermsConditionsEntity termsConditionsEntity, Boolean agreement, Long userInfoId);

    List<TermsConditionsResponse> toTermsConditionsResponseList(
            List<TermsConditionsEntity> termsConditionsEntityList);
}
