package com.example.mymall.member.vo;

import lombok.Data;

@Data
public class WeiboSocialUserVO {

    private String access_token;

    private Long expires_in;

    private String remind_in;

    private String uid;

    private String isRealName;
}
