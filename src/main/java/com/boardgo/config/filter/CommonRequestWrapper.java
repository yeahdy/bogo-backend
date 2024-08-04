package com.boardgo.config.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.springframework.util.StreamUtils;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class CommonRequestWrapper extends HttpServletRequestWrapper {
	private final String REQUEST_DATA;

	public CommonRequestWrapper(HttpServletRequest request) throws IOException {
		super(request);
		REQUEST_DATA = getRequestData(request);
	}

	private String getRequestData(HttpServletRequest request) throws IOException {
		return StreamUtils.copyToString(
			request.getInputStream(), Charset.forName(request.getCharacterEncoding())
		);
	}

	@Override
	public ServletInputStream getInputStream() {
		ByteArrayInputStream newInputStream = new ByteArrayInputStream(this.REQUEST_DATA.getBytes());
		return new ServletInputStream() {

			@Override
			public int read() {
				return newInputStream.read();
			}

			@Override
			public boolean isFinished() {
				return newInputStream.available() == 0;
			}

			@Override
			public boolean isReady() {
				return true;
			}

			@Override
			public void setReadListener(ReadListener listener) {    //비동기 지원x
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public BufferedReader getReader() throws IOException {
		//InputStream을 문자 스트림으로 변환
		InputStreamReader inputStreamReader = new InputStreamReader(this.getInputStream());
		return new BufferedReader(inputStreamReader);
	}
}
