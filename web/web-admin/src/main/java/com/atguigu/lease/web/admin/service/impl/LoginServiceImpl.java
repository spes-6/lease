package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.common.utils.JwtUtil;
import com.atguigu.lease.model.entity.SystemUser;
import com.atguigu.lease.model.enums.BaseStatus;
import com.atguigu.lease.web.admin.mapper.SystemUserMapper;
import com.atguigu.lease.web.admin.service.LoginService;
import com.atguigu.lease.web.admin.vo.login.CaptchaVo;
import com.atguigu.lease.web.admin.vo.login.LoginVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wf.captcha.SpecCaptcha;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private SystemUserMapper systemUserMapper;

    @Override
    public CaptchaVo getCaptcha() {
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 4);
        String code = specCaptcha.text().toLowerCase();
        String key = RedisConstant.ADMIN_LOGIN_PREFIX + UUID.randomUUID();
        stringRedisTemplate.opsForValue().set(key, code,RedisConstant.APP_LOGIN_CODE_RESEND_TIME_SEC, TimeUnit.SECONDS);
        return new CaptchaVo(specCaptcha.toBase64(), key);
    }

    @Override
    public String login(LoginVo loginVo) {
        if(loginVo.getCaptchaCode()==null){
            throw new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_NOT_FOUND);
        }
        String code = stringRedisTemplate.opsForValue().get(loginVo.getCaptchaKey());
        if(code==null){throw new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_EXPIRED);}
        //转换为小写
        if(!loginVo.getCaptchaCode().toLowerCase().equals(code)){throw new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_ERROR);}
        //验证用户名
        LambdaQueryWrapper<SystemUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemUser::getUsername,loginVo.getUsername());
        SystemUser systemUser = systemUserMapper.selectOneByUserName(loginVo.getUsername());
        if(systemUser==null){throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR);}
        //状态
        if(systemUser.getStatus()== BaseStatus.DISABLE){throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_DISABLED_ERROR);}
        //验证密码
        if(!systemUser.getPassword().equals(DigestUtils.md5Hex(loginVo.getPassword()))){throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_ERROR);}

        //发放jwt令牌
        return JwtUtil.creatToken(systemUser.getId(),systemUser.getUsername());



    }
}
