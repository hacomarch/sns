package com.example.sns.configuration.filter;

import com.example.sns.model.User;
import com.example.sns.service.UserService;
import com.example.sns.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//요청이 서버에 도착할 때 jwt를 검증하고, 유효한 경우 해당 사용자를 인증하는 필터
@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final String key;
    private final UserService userService;

    //모든 HTTP 요청에 대해 한 번만 실행되는 메서드, 요청을 처리하고 jwt를 확인하고 사용자를 인증함.
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String header = request.getHeader(HttpHeaders.AUTHORIZATION); //요청 헤더에서 AUTHORIZATION 헤더를 가져옴, 이 헤더는 jwt 토큰을 포함하고 있어, 클라이언트가 토큰을 전송함
        if (header == null || !header.startsWith("Bearer ")) { //헤더가 없거나, Bearer 로 시작하지 않으면,
            log.error("Error occurs while getting header. header is null or invalid");
            filterChain.doFilter(request, response); //요청이 잘못된 것으로 간주하고 필터 체인을 계속 진행시킴.
            return;
        }

        try {
            final String token = header.split(" ")[1].trim(); //Bearer 다음의 jwt 토큰을 추출

            //check token is valid
            if (JwtTokenUtils.isExpired(token, key)) { //토큰이 만료 되었으면,
                log.error("Key is expired");
                filterChain.doFilter(request, response); //필터 체인 계속 진행
                return;
            }

            //get username from token
            String userName = JwtTokenUtils.getUserName(token, key); //jwt 토큰에서 사용자 이름 저장

            //check the user is valid
            User user = userService.loadUserByUserName(userName); //사용자 이름을 사용해 사용자 정보를 db에서 가져옴

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities()
            );//사용자 인증하고 사용자 권한 설정
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); //현재 요청에 대한 사용자의 인증 세부 정보 설정, 인증 객체에 사용자와 관련된 추가 정보 추가
            SecurityContextHolder.getContext().setAuthentication(authentication); //보안 컨텍스트에 사용자 정보 설정
        } catch (RuntimeException e) {
            log.error("Error occurs while validating. {}", e.toString());
            filterChain.doFilter(request, response); //예외일 때 필터 체인 계속 진행
            return;
        }

        filterChain.doFilter(request, response); // 모든 검증 및 인증이 완료되면, 필터 체인을 계속 진행시켜 요청을 다음 단계로 전달함.

    }
}
