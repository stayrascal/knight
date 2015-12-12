package com.knight.module.auth.entity;

import com.knight.core.annotation.MetaData;
import com.knight.core.cons.GlobalConstant;
import com.knight.core.entity.BaseNativeEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * Date: 2015/11/22
 * Time: 23:27
 *
 * @author Rascal
 */
@Setter
@Getter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "auth_userr2role", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"}))
@MetaData(value = "登陆账号与角色关联")
public class UserR2Role extends BaseNativeEntity {

    private static final long serialVersionUID = -8910751033894285816L;

    @MetaData(value = "登陆账号对象")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = GlobalConstant.GlobalForeignKeyName))
    private User user;

    @MetaData(value = "关联角色对象")
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(name = GlobalConstant.GlobalForeignKeyName))
    private Role role;

    @Transient
    @Override
    public String getDisplay() {
        return user.getDisplay() + "_" + role.getDisplay();
    }
}
