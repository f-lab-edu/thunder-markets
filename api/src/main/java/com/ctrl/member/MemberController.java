package com.ctrl.member;

import com.model.member.UserDTO;
import com.tmk.prj.ApiApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/auth")
public class MemberController {

    private static final Logger logger = Logger.getLogger(ApiApplication.class.getName());

    @GetMapping("/login")
    public List<UserDTO> loginUser() {
        List<UserDTO> userInfo = UserDTO.dummyData();
        logger.log(Level.INFO, "로그인 결과 : " + userInfo.toString());
        return userInfo;
    }

}
