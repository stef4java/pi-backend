package com.springai.pi.service;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.springai.pi.domain.LoginUser;
import com.springai.pi.domain.SysUser;
import com.springai.pi.domain.bo.RegisterBo;
import com.springai.pi.domain.vo.LoginVo;
import com.springai.pi.domain.vo.SysUserVo;
import com.springai.pi.exception.ServiceException;
import com.springai.pi.exception.UserException;
import com.springai.pi.mapper.SysUserMapper;
import com.springai.pi.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 登录校验方法
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class UserAuthService {

    private final SysUserMapper sysUserMapper;

    public LoginVo checkLogin(String username, String password) {
        SysUserVo user = sysUserMapper.selectVoOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName, username));
        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在.", username);
            throw new UserException("user.not.exists", username);
        }
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new UserException("密码错误");
        }
        LoginUser loginUser = buildLoginUser(user);

        SaLoginParameter model = new SaLoginParameter();
        // 自定义分配 不同用户体系 不同 token 授权时间 不设置默认走全局 yml 配置
        // 例如: 后台用户30分钟过期 app用户1天过期
        model.setTimeout(604800);
        model.setActiveTimeout(1800);
        // 生成token
        LoginHelper.login(loginUser, model);

        LoginVo loginVo = new LoginVo();
        loginVo.setAccessToken(StpUtil.getTokenValue());
        loginVo.setExpireIn(StpUtil.getTokenTimeout());
        return loginVo;
    }

    /**
     * 构建登录用户
     */
    public LoginUser buildLoginUser(SysUserVo user) {
        LoginUser loginUser = new LoginUser();
        Long userId = user.getUserId();
        loginUser.setUserId(userId);
        loginUser.setUsername(user.getUserName());
        loginUser.setNickname(user.getNickName());
        return loginUser;
    }

    public void register(RegisterBo user) {
        String username = user.getUsername();
        String password = user.getPassword();
        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);
        sysUser.setNickName(username);
        sysUser.setPassword(BCrypt.hashpw(password));
        boolean exist = sysUserMapper.exists(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserName, sysUser.getUserName()));
        if (exist) {
            throw new ServiceException("用户名已存在");
        }
        boolean regFlag = sysUserMapper.insert(sysUser) > 0;
        if (!regFlag) {
            throw new ServiceException("注册失败，请重试");
        }
    }


    /**
     * 退出登录
     */
    public void logout() {
        try {
            LoginUser loginUser = LoginHelper.getLoginUser();
            if (Objects.isNull(loginUser)) {
                return;
            }
        } catch (NotLoginException e) {
        } finally {
            try {
                StpUtil.logout();
            } catch (NotLoginException ignored) {
            }
        }
    }


}