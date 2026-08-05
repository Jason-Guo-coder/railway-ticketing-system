package com.gjq.train.business.controller.admin;

import com.gjq.train.business.req.AdminLoginReq;
import com.gjq.train.business.resp.AdminLoginResp;
import com.gjq.train.common.exception.BusinessException;
import com.gjq.train.common.exception.BusinessExceptionEnum;
import com.gjq.train.common.resp.Result;
import com.gjq.train.common.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final String adminUsername;

    private final String adminPassword;

    private final String jwtSecret;

    public AdminController(
            @Value("${admin.username}") String adminUsername,
            @Value("${admin.password}") String adminPassword,
            @Value("${jwt.secret}") String jwtSecret
    ) {
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.jwtSecret = jwtSecret;
    }

    @PostMapping("/login")
    public Result<AdminLoginResp> login(
            @Valid @RequestBody AdminLoginReq request
    ) {
        //1. 校验管理员账号和密码
        if (!adminUsername.equals(request.getUsername())
                || !adminPassword.equals(request.getPassword())) {
            throw new BusinessException(
                    BusinessExceptionEnum.ADMIN_LOGIN_ERROR
            );
        }

        //2. 签发带管理员角色的Token
        AdminLoginResp response = new AdminLoginResp();
        response.setUsername(adminUsername);
        response.setToken(
                JwtUtil.createAdminToken(adminUsername, jwtSecret)
        );
        return Result.success(response);
    }
}
