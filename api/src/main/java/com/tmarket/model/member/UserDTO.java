package com.tmarket.model.member;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserDTO {

    @Column(name="userId", nullable = false, length = 30)
    private String userId;

    @Column(name="name", nullable = false, length = 30)
    private String name;

    @Column(name="email", nullable = false, length = 30)
    private String email;

    @Column(name="password", nullable = false, length = 30)
    private String password;

    @Column(name="memberStts", nullable = false, length = 10)
    private String memberStts;

    @Column(name="regDt", nullable = false, length = 15)
    private Date regDt;

    @Column(name="modDt", nullable = false, length = 15)
    private Date modDt;

    @Column(name="lastDt", nullable = false, length = 15)
    private Date lastDt;

    @Column(name="isActive", nullable = false, length = 2)
    private Boolean isActive;

    public UserDTO(String userId, String name, String email, String password, String memberStts, Date regDt, Date modDt, Date lastDt, Boolean isActive) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.memberStts = memberStts;
        this.regDt = regDt;
        this.modDt = modDt;
        this.lastDt = lastDt;
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", memberStts='" + memberStts + '\'' +
                ", regDt=" + regDt +
                ", modDt=" + modDt +
                ", lastDt=" + lastDt +
                ", isActive=" + isActive +
                '}';
    }
}
