package com.example.sns.exception;

import com.example.sns.controller.response.Response;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//AuthenticationEntryPoint : 사용자가 인증되지 않았거나 실패한 경우, 어떻게 처리할 지 정의하는 인터페이스
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint{

    //사용자가 인증되지 않았거나 실패한 경우 호출되는 메서드
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException{
        response.setContentType("application/json");
        response.setStatus(ErrorCode.INVALID_TOKEN.getStatus().value()); //에러 코드를 사용해 상태 코드 설정
        response.getWriter().write(Response.error(ErrorCode.INVALID_TOKEN.name()).toStream()); //응답 본문 작성,
        //INVALID_TOKEN 오류에 대한 JSON 형식의 응답 메시지를 작성해 클라이언트에게 반환
    }
}
