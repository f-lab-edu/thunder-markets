package com.tmarket.model.member;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserDTO {
    @Column(name="name", nullable = false, length = 30)
    private String name;

    @Column(name="id", length = 30)
    private String id;

    @Column(name="pwd", nullable = false, length = 30)
    private String pwd;

    @Override
    public String toString() {
        return name + "\t" + id + "\t" + pwd;
    }

    public static List<UserDTO> dummyData() {
        List<UserDTO> users = new ArrayList<>();

        UserDTO user = new UserDTO();
        user.setName("최병권");
        user.setId("user123");
        user.setPwd("password123");

        users.add(user);

        return users;
    }
}
