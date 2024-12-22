package com.tmarket.controller.member;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.tmarket.model.member.UserDTO;

@RestController
@RequestMapping("/auth")
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @GetMapping("/login")
    public List<UserDTO> loginUser() {

        logger.info("===================================================");
        logger.info("===================================================");
        logger.info("===================================================");
        logger.info("========thunder-market 에 오신것을 환영합니다.========");
        logger.info("===================================================");
        logger.info("===================================================");
        logger.info("===================================================");

        List<UserDTO> userInfo = UserDTO.dummyData();
        for(UserDTO user : userInfo) {
            logger.info("=====================로그인 성공=====================");
            logger.info("**** 로그인 정보 **** ");
            logger.info("이름 : " + user.getName());
            logger.info("아이디 : " +  user.getId());
            logger.info("비밀번호 : " + user.getPwd());
        }
        return userInfo;
    }
}
