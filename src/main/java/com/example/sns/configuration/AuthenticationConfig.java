package com.example.sns.configuration;

import com.example.sns.configuration.filter.JwtTokenFilter;
import com.example.sns.exception.CustomAuthenticationEntryPoint;
import com.example.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity //spring security를 사용해 웹 보안 활성화
@RequiredArgsConstructor
public class AuthenticationConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    @Value("${jwt.secret-key}")
    private String key;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().regexMatchers("^(?!/api/).*");
    }

    //웹 보안 구성 정의
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable() //csrf 토큰 검사 X
                .authorizeRequests() //요청에 대한 권한 및 인증 규칙 정의
                .antMatchers("/api/*/users/join", "/api/*/users/login").permitAll() //설정한 경로에 대한 요청은 모든 사용자에게 허용됨
                .antMatchers("/api/**").authenticated() //설정한 경로에 대한 모든 요청은 인증된 사용자만 허용
                .and()
                .sessionManagement()// 세션 관리 설정
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)// 세션 생성하지 않고, 상태를 유지하지 않게 설정
                .and()
                .addFilterBefore(new JwtTokenFilter(key, userService), UsernamePasswordAuthenticationFilter.class) //jwt 토큰 검증하고 사용자 인증
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()); //사용자 인증이 실패한 경우 CustomAuthenticationEntryPoint를 호출 적절한 오류 응답 생성
    }
}
