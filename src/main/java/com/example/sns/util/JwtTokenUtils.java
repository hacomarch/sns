package com.example.sns.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

//JWT : 인증 및 권한 부여를 위한 토큰 기반의 인증 시스템에서 사용됨
//사용자의 정보와 유효 기간 정보를 포함하는 안전한 토큰 생성
public class JwtTokenUtils {

    public static String getUserName(String token, String key) { //토큰에 저장된 유저 이름 확인
        return extractClaims(token, key).get("userName", String.class);
    }

    public static boolean isExpired(String token, String key) { //만료 여부 확인
        Date expiredDate = extractClaims(token, key).getExpiration(); //클레임의 만료 일자 저장
        return expiredDate.before(new Date()); //만료 일자와 현재 날짜와 시간을 비교해 토큰 만료 여부 판단
        //만료 일자가 현재 날짜 이전인 경우(만료됨) true, else false
    }

    private static Claims extractClaims(String token, String key) { //클레임 추출
        return Jwts.parserBuilder().setSigningKey(getKey(key)) //jwt parser 생성 -> 서명 키 생성 및 설정
                .build().parseClaimsJws(token).getBody(); //토큰을 파싱하고 서명 확인, 서명이 유효하면 jwt 객체 반환 -> 객체에서 클레임 추출
    }

    public static String generateToken(String userName, String key, long expiredTimeMs) {
        Claims claims = Jwts.claims(); //데이터 조각, 클레임을 읽고 검증하거나 해석함
        claims.put("userName", userName);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis())) //발행 일자
                .setExpiration(new Date(System.currentTimeMillis() + expiredTimeMs)) //만료일자
                .signWith(getKey(key), SignatureAlgorithm.HS256) //해시 알고리즘을 사용해 키를 기반으로 사인
                .compact(); //문자열로 반환
    }

    //키 생성 메서드
    private static Key getKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8); //키를 UTF_8 인코딩을 사용해 바이트 배열로 변환
        return Keys.hmacShaKeyFor(keyBytes);//그 배열을 HMAC SHA-256키로 변환해 반환
    }
}
