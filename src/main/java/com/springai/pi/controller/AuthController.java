package com.springai.pi.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.springai.pi.domain.bo.RegisterBo;
import com.springai.pi.domain.core.R;
import com.springai.pi.domain.bo.LoginBo;
import com.springai.pi.domain.vo.LoginVo;
import com.springai.pi.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器  https://gitee.com/dromara/RuoYi-Vue-Plus.git
 *
 * @author stef
 */
@Validated
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@SaIgnore
public class AuthController {
    private final UserAuthService userAuthService;

    @SaIgnore
    @PostMapping("/login")
    public R<LoginVo> login(@Validated @RequestBody LoginBo loginBo) {
        LoginVo loginVo = userAuthService.checkLogin(loginBo.getUsername(), loginBo.getPassword());
        return R.ok(loginVo);
    }

    /**
     * 用户注册
     */
    @SaIgnore
    @PostMapping("/register")
    public R<Void> register(@Validated @RequestBody RegisterBo user) {
        userAuthService.register(user);
        return R.ok();
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public R<Void> logout() {
        userAuthService.logout();
        return R.ok("退出成功");
    }


}
