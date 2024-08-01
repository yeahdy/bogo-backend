package com.boardgo.integration.user.controller;

import static io.restassured.RestAssured.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.boardgo.common.constant.HeaderConstant;
import com.boardgo.domain.user.controller.dto.SignupRequest;
import com.boardgo.integration.support.RestDocsTestSupport;

public class SignupDocsTest extends RestDocsTestSupport {

	@Test
	@DisplayName("사용자는 회원가입을 진행할 수 있다V1")
	void 사용자는_회원가입을_진행할_수_있다V1() {
		//given
		SignupRequest signupRequest = new SignupRequest("aa@aa.aa", "nickname", "password",
			List.of("prTag1", "prTag2"));
		given(this.spec)
			.log().all()
			.port(port)
			.header(HeaderConstant.API_VERSION_HEADER, "1")
			.contentType(MediaType.APPLICATION_JSON_VALUE)
			.body(signupRequest)
			.filter(document("회원가입",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("email").type(STRING).description("이메일 주소 형식에 맞아야합니다!"),
					fieldWithPath("nickName").type(STRING).description("닉네임"),
					fieldWithPath("password").type(STRING).description("8자 이상 50자 이하"),
					fieldWithPath("prTags").type(ARRAY).description("회원가입 시 입력한 태그들 (각 요소는 빈 문자열일 수 없습니다)").optional()
				)
			))
			.when()
			.post("/signup")
			.then()
			.statusCode(HttpStatus.CREATED.value());
	}
}
