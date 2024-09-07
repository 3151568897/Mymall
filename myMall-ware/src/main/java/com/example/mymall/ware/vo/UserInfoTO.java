package com.example.mymall.ware.vo;

import lombok.Data;

@Data
public class UserInfoTO {
    /**
     * 登录了就会有userId,没有登录就会有userKey
     */
    private Long userId;

    private String userKey;
    //是否已经保存到cookie
    private boolean tempUser = false;
}
