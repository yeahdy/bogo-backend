package com.boardgo.domain.user.repository;

import java.util.List;

public interface UserPrTagJdbcRepository {
	void bulkInsertPrTags(List<String> prTags, Long userInfoId);
}
