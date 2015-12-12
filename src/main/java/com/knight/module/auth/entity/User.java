package com.knight.module.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.knight.core.annotation.MetaData;
import com.knight.core.cons.GlobalConstant;
import com.knight.core.entity.BaseNativeEntity;
import com.knight.core.util.DateUtils;
import com.knight.core.web.json.DateTimeJsonSerializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.NotAudited;
import org.hibernate.validator.constraints.Email;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * Date: 2015/11/22
 * Time: 21:55
 *
 * @author Rascal
 */
@Setter
@Getter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "auth_user", uniqueConstraints = @UniqueConstraint(columnNames = {"authUid", "authType"}))
@MetaData(value = "登陆认证账号信息")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User extends BaseNativeEntity {

    private static final long serialVersionUID = 8958496955301228368L;

    @MetaData(value = "账号全局唯一标识", comments = "同时作为SYS类型用户登录密码的SALT")
    @Column(length = 64, nullable = false, unique = true)
    private String authGuid;

    @MetaData(value = "账号类型所对应唯一标识")
    @Column(length = 64, nullable = false)
    private String authUid;

    @MetaData(value = "账号类型")
    @Column(length = 8, nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthTypeEnum authType = AuthTypeEnum.SYS;

    @MetaData(value = "管理授权", tooltips = "标识用户是否可访问管理端")
    private Boolean mgmtGranted = Boolean.FALSE;

    @MetaData(value = "登陆后友好显示昵称")
    private String nickName;

    @MetaData(value = "REST访问Token")
    private String accessToken;

    @MetaData(value = "访问Token过期时间")
    private Timestamp accessTokenExpireTime;

    @MetaData(value = "失效日期", tooltips = "设定账号访问系统的失效日期，为空表示永不失效")
    @DateTimeFormat(pattern = DateUtils.DEFAUlT_DATE_FORMAT)
    @JsonSerialize(using = DateTimeJsonSerializer.class)
    private Timestamp accountExpireTime;

    @MetaData(value = "账户未锁定标识", tooltips = "账号锁定后无法登陆")
    private Byte accountNonLocked;

    @MetaData(value = "账户密码过期时间", tooltips = "到期后强制用户登陆成功后必须修改密码", comments = "比如用于初始化密码时设置当前时间，这样用户下次登陆成功后则强制用户必须修改密码")
    private Timestamp credentialsExpireTime;

    @MetaData(value = "电子邮件", tooltips = "请仔细填写，可用于系统通知邮件发送，找回密码等功能")
    @Email
    private String email;

    @MetaData(value = "最近失败认证时间")
    private Timestamp lastLogonFailureTime;

    @MetaData(value = "最后登录主机名")
    private String lastLogonHost;

    @MetaData(value = "最后登陆IP")
    private String lastLogonIp;

    @MetaData(value = "最后登陆时间")
    private Timestamp lastLogonTime;

    @MetaData(value = "最近认证失败次数", comments = "认证失败累加，成功后清零。达到设定失败次数后锁定账号，防止无限制次数尝试猜测密码")
    private Integer logonFailureTimes;

    @MetaData(value = "总计登陆次数")
    private Integer logonTimes;

    @MetaData(value = "移动电话", tooltips = "请仔细填写，可用于系统通知短信发送，找回密码等功能")
    private String mobile;

    @MetaData(value = "SYS自建类型用户的密码", comments = "加密算法： MD5（{authGuid} + 原始密码）")
    private String password;

    @MetaData(value = "随机数", comments = "用于找回密码设定的随机UUID字符串")
    private String randomcode;

    @MetaData(value = "注册时间")
    @JsonSerialize(using = DateTimeJsonSerializer.class)
    private Timestamp signupTime;

    @MetaData(value = "绑定目标账号", comments = "用于OAUTH类型账号绑定到SYS类型累加管理角色")
    @NotAudited
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinColumn(name = "bindTo_id", foreignKey = @ForeignKey(name = GlobalConstant.GlobalForeignKeyName))
    @JsonIgnore
    private User bindToId;

    @MetaData(value = "角色关联")
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    @JsonIgnore
    private List<UserR2Role> userR2Roles;

    @MetaData(value = "已关联角色主键集合", comments = "辅助属性：用于页面表单标签进行数据绑定")
    @Transient
    @Getter(AccessLevel.NONE)
    @JsonIgnore
    private Long[] selectedRoleIds;

    public Long[] getSelectedRoleIds() {
        if (userR2Roles != null && selectedRoleIds == null) {
            selectedRoleIds = new Long[userR2Roles.size()];
            for (int i = 0; i < selectedRoleIds.length; i++) {
                selectedRoleIds[i] = userR2Roles.get(i).getRole().getId();
            }
        }
        return selectedRoleIds;
    }

    @Transient
    @Override
    public String getDisplay() {
        return authType + "-" + authUid;
    }

    @MetaData(value = "用户标识别名", comments = "用户在多个终端登录，需要一个标识同一个身份以便多终端推送消息")
    @Transient
    public String getAlias() {
        return authType + "_" + authUid;
    }

    public enum AuthTypeEnum {
        @MetaData(value = "自建账号")
        SYS,

        @MetaData(value = "QQ互联")
        QQ,

        @MetaData(value = "新浪微博")
        WB;
    }
}
