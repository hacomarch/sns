package com.example.sns.model.entity;

import com.example.sns.model.UserRole;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "user")
@Getter
@Setter
@SQLDelete(sql = "UPDATED user SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL") //null인 행만 가져오기 == 삭제되지 않은 항목만 검색
public class UserEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_name")
    private String userName;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    @Column(name = "registered_at")
    private Timestamp registeredAt; //등록된 시간

    @Column(name = "updated_at")
    private Timestamp updatedAt; //수정된 시간

    @Column(name = "deleted_at")
    private Timestamp deletedAt; //삭제된 시간

    @PrePersist
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    public static UserEntity of(String userName, String password) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(userName);
        userEntity.setPassword(password);
        return userEntity;
    }

}
