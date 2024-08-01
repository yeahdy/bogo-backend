package com.boardgo.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserPrTagMapper {
	UserPrTagMapper userPrTagMapper = Mappers.getMapper(UserPrTagMapper.class);
	
}
