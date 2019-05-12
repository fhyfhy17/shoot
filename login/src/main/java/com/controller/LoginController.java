package com.controller;


import com.entry.UserEntry;
import com.exception.StatusException;
import com.net.msg.LOGIN_MSG;
import com.service.LoginService;
import com.template.templates.type.TipType;
import com.util.CountUtil;
import com.util.TipStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Objects;

@Controller
public class LoginController extends BaseController {
    @Autowired
    private LoginService loginService;

    public LOGIN_MSG.GTC_LOGIN login(UidContext context, LOGIN_MSG.CTG_LOGIN req) throws StatusException {
        String username = req.getUsername();
        String password = req.getPassword();
        String sessionId = req.getSessionId();

        LOGIN_MSG.GTC_LOGIN.Builder builder = LOGIN_MSG.GTC_LOGIN.newBuilder();
        UserEntry user = loginService.login(username, password);
        builder.setSessionId(sessionId);
        if (!Objects.isNull(user)) {
            builder.setUid(user.getId());
            builder.setResult(TipStatus.suc());
        } else {
            builder.setResult(TipStatus.fail(TipType.AccountError));
        }

        return builder.build();

    }


    public void login(UidContext context, LOGIN_MSG.TEST_TIME req) {
        CountUtil.count();
        String username = req.getMsg();


    }
}
