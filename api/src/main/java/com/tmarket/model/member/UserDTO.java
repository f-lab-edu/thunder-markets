package com.tmarket.model.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {

    private String userId;

    private String name;

    private String email;

    private String password;

    private String memberStatus;

    private Date registDate;

    private Date modifyDate;

    private Date deleteDate;

    private Date lastLoginDate;

    private Boolean isActive;
}
