package com.example.sns.model.entity;

import com.example.sns.model.AlarmArgs;
import com.example.sns.model.AlarmType;
import com.fasterxml.jackson.annotation.JsonTypeId;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "alarm")
@Getter
@Setter
@SQLDelete(sql = "UPDATE alarm SET deleted_at = NOW() where id=?")
@Where(clause = "deleted_at is NULL")
@TypeDef(name = "json", typeClass = JsonType.class) //Hibernate에서 사용자 정의 타입을 매핑하는데 사용, 여기서는 json 타입을 사용하기 위함.
public class AlarmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user; //알람을 받는 사람

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    @Type(type = "json") //위에서 정의한 @TypeDef와 매핑됨.
    @Column(columnDefinition = "json")
    private AlarmArgs args;

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

    public static AlarmEntity of(UserEntity userEntity, AlarmType alarmType, AlarmArgs args) {
        AlarmEntity entity = new AlarmEntity();
        entity.setUser(userEntity);
        entity.setAlarmType(alarmType);
        entity.setArgs(args);
        return entity;
    }
}
