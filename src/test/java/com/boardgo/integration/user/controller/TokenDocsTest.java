package com.boardgo.integration.user.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER;
import static com.boardgo.common.constant.HeaderConstant.AUTHORIZATION;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import com.boardgo.integration.support.RestDocsTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.cookies.RequestCookiesSnippet;
import org.springframework.restdocs.headers.RequestHeadersSnippet;
import org.springframework.restdocs.headers.ResponseHeadersSnippet;

public class TokenDocsTest extends RestDocsTestSupport {

    @Test
    @DisplayName("쿠키에 담긴 토큰을 헤더로 전달한다")
    void 쿠키에_담긴_토큰을_헤더로_전달한다() {
        String accessToken = testAccessToken.split("\s")[1];
        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .cookie(AUTHORIZATION, accessToken)
                .filter(
                        document(
                                "token",
                                preprocessResponse(prettyPrint()),
                                getRequestHeadersSnippet(),
                                getRequestCookiesSnippet(),
                                getResponseHeadersSnippet()))
                .when()
                .get("/token")
                .then()
                .header(AUTHORIZATION, testAccessToken)
                .statusCode(HttpStatus.CREATED.value());
    }

    private RequestHeadersSnippet getRequestHeadersSnippet() {
        return requestHeaders(headerWithName(API_VERSION_HEADER).description("버전 정보"));
    }

    private RequestCookiesSnippet getRequestCookiesSnippet() {
        return requestCookies(cookieWithName(AUTHORIZATION).description("Access Token"));
    }

    private ResponseHeadersSnippet getResponseHeadersSnippet() {
        return responseHeaders(headerWithName(AUTHORIZATION).description("JWT Access Token"));
    }
}
