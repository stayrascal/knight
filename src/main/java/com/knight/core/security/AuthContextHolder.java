package com.knight.core.security;

import com.knight.core.context.SpringContextHolder;
import com.knight.module.auth.entity.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.knight.module.auth.entity.User.AuthTypeEnum;


/**
 * 以ThreadLocal方式实现Web端登陆信息传递到业务的存取
 * <p>
 * Date: 2015/11/20
 * Time: 13:19
 *
 * @author Rascal
 */
public class AuthContextHolder {

    private static final Logger logger = LoggerFactory.getLogger(AuthContextHolder.class);

    public static final String DEFAULT_UNKNOWN_PIN = "N/A";

    public static String getAuthSysUserUid() {
        AuthUserDetails authUserDetails = getAuthUserDetails();
        if ((authUserDetails == null) || AuthTypeEnum.SYS.equals(authUserDetails.getAuthType())){
            return null;
        }
        return authUserDetails.getAuthUid();
    }

    /**
     * 获取登录用户的友好显示字符串
     */
    public static String getUserDisplay() {
        AuthUserDetails authUserDetails = getAuthUserDetails();
        return authUserDetails != null ? authUserDetails.getAuthDisplay() : DEFAULT_UNKNOWN_PIN;
    }

    /**
     * 基于Spring Security获取用户认证信息
     */
    public static AuthUserDetails getAuthUserDetails() {
        Subject subject = null;
        try {
            subject = SecurityUtils.getSubject();
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
        if (subject == null) {
            return null;
        }
        Object princiapl = subject.getPrincipal();
        return princiapl == null ? null : (AuthUserDetails) princiapl;
    }

    public static User findAuthUser(){
        AuthUserDetails authUserDetails = AuthContextHolder.getAuthUserDetails();
        if (authUserDetails == null){
            return null;
        }
        UserService userService = SpringContextHolder.getBean(UserService.class);
    }
}
