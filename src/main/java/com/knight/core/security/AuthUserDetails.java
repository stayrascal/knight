package com.knight.core.security;

import com.knight.core.annotation.MetaData;
import com.knight.module.auth.entity.User.AuthTypeEnum;
import com.knight.core.security.SourceUsernamePasswordToken.AuthSourceEnum;
import java.io.Serializable;

/**
 * 存放在权限框架容器中的认证授权用户数据对象
 * <p>
 * Date: 2015/11/20
 * Time: 13:22
 *
 * @author Rascal
 */
public class AuthUserDetails implements Serializable {

    private static final long serialVersionUID = 8877830699250228464L;

    @MetaData(value = "超级管理员角色")
    public final static String ROLE_SUPER_USER = "ROLE_SUPER_USER";

    @MetaData(value = "前端门户用户角色")
    public final static String ROLE_SITE_USER = "ROLE_SITE_USER";

    @MetaData(value = "APP用户角色")
    public final static String ROLE_APP_USER = "ROLE_APP_USER";

    @MetaData(value = "后端管理用户角色")
    public final static String ROLE_MGMT_USER = "ROLE_MFMT_USER";
    @MetaData(value = "所有受控权限赋予此角色")
    public final static String ROLE_PROTECTED = "ROLE_PROTECTED";

    @MetaData(value = "账号全局唯一标识")
    private String authGuid;

    @MetaData(value = "账号类型所对应唯一标识")
    private String authUid;

    @MetaData(value = "账号类型")
    private AuthTypeEnum authType;

    @MetaData(value = "登陆后友好显示昵称")
    private String nickname;

    @MetaData(value = "记录登陆来源")
    private AuthSourceEnum source = AuthSourceEnum.W;

    @MetaData(value = "访问Token")
    private String accessToken;

    public String getAuthGuid() {
        return authGuid;
    }

    public void setAuthGuid(String authGuid) {
        this.authGuid = authGuid;
    }

    public String getAuthUid() {
        return authUid;
    }

    public void setAuthUid(String authUid) {
        this.authUid = authUid;
    }

    public AuthTypeEnum getAuthType() {
        return authType;
    }

    public void setAuthType(AuthTypeEnum authType) {
        this.authType = authType;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public AuthSourceEnum getSource() {
        return source;
    }

    public void setSource(AuthSourceEnum source) {
        this.source = source;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAuthDisplay(){
        return authType + ":" + authUid;
    }

    public String getUrlPrefixBySource(){
        if (AuthSourceEnum.A.equals(source)){
            return "/admin";
        } else if (AuthSourceEnum.M.equals(source)){
            return "/m";
        } else {
            return "/w";
        }
    }

    @Override
    public String toString() {
        return "AuthUserDetails{" +
                "authGuid='" + authGuid + '\'' +
                ", authUid='" + authUid + '\'' +
                ", authType=" + authType +
                ", nickname='" + nickname + '\'' +
                ", source=" + source +
                ", accessToken='" + accessToken + '\'' +
                '}';
    }
}
