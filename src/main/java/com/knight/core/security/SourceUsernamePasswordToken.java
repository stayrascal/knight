package com.knight.core.security;

import com.knight.core.annotation.MetaData;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * Date: 2015/11/23
 * Time: 11:47
 *
 * @author Rascal
 */
public class SourceUsernamePasswordToken extends UsernamePasswordToken {

    private static final long serialVersionUID = 4629111014942819208L;

    public SourceUsernamePasswordToken(String username, String password, boolean rememberMe, String host) {
        super(username, password, rememberMe, host);
    }

    @MetaData(value = "登陆来源", comments = "标识是前端用户或者后端管理等登陆来源，可根据不同来源授权默认角色")
    private AuthSourceEnum source;

    @MetaData(value = "来源唯一标识", comments = "标识来源设备或应用唯一标识")
    private String uid;

    public AuthSourceEnum getSource() {
        return source;
    }

    public void setSource(AuthSourceEnum source) {
        this.source = source;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public static enum AuthSourceEnum {

        @MetaData(value = "APP手机应用")
        P,

        @MetaData(value = "HTML5 Mobile站点")
        M,

        @MetaData(value = "WWW主站", comments = "sourcesource来源为空也表示此类型")
        W,

        @MetaData(value = "Admin管理端")
        A;
    }
}
