package com.example.sns.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "post")
@Getter
@Setter
@SQLDelete(sql = "UPDATE post SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL") //null인 행만 가져오기 == 삭제되지 않은 항목만 검색
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

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

    public static PostEntity of(String title, String body, UserEntity userEntity) {
        PostEntity entity = new PostEntity();
        entity.setTitle(title);
        entity.setBody(body);
        entity.setUser(userEntity);
        return entity;
    }

}
