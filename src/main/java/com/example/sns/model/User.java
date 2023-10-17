package com.example.sns.model;

import com.example.sns.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

//DTO
@AllArgsConstructor
@Getter
public class User implements UserDetails {

    private Integer id;
    private String userName;
    private String password;
    private UserRole userRole;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static User fromEntity(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getUserName(),
                entity.getPassword(),
                entity.getRole(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { //사용자의 권한 목록 반환
        return List.of(new SimpleGrantedAuthority(this.getUserRole().toString()));
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() { //만료 여부
        return this.deletedAt == null;
    }

    @Override
    public boolean isAccountNonLocked() { //잠금 여부
        return this.deletedAt == null;
    }

    @Override
    public boolean isCredentialsNonExpired() { //자격 증명(비밀번호) 만료 여부
        return this.deletedAt == null;
    }

    @Override
    public boolean isEnabled() { //활성화 여부
        return this.deletedAt == null;
    }
}
