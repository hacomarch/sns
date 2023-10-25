package com.example.sns.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlarmArgs {

    private Integer fromUserId; //알람을 보낸 유저, 알람을 발생시킨 유저

    private Integer targetId;
}
