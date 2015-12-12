package com.knight.module.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.knight.core.annotation.MetaData;
import com.knight.core.entity.BaseNativeEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;


/**
 * Date: 2015/11/22
 * Time: 23:30
 *
 * @author Rascal
 */
@Setter
@Getter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "auth_role")
@MetaData(value = "角色")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Role extends BaseNativeEntity {

    private static final long serialVersionUID = -3380771253969724493L;

    @MetaData(value = "代码", tooltips = "必须y以ROLE_打头")
    @Size(min = 6)
    @Pattern(regexp = "^ROLE_.*", message = "必须以[ROLE_]开头")
    @Column(nullable = false, length = 64, unique = true)
    private String code;

    @MetaData(value = "名称")
    @Column(nullable = false, length = 256)
    private String name;

    @MetaData(value = "禁用标识", tooltips = "禁用角色不参与权限控制逻辑")
    private Boolean disabled = Boolean.FALSE;

    @MetaData(value = "描述")
    @Column(nullable = true, length = 2000)
    private String description;

    @MetaData(value = "角色权限关联")
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    @JsonIgnore
    private List<RoleR2Privilege> roleR2Privileges = Lists.newArrayList();

    @MetaData(value = "角色管理用户")
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    @JsonIgnore
    private List<UserR2Role> roleR2Users = Lists.newArrayList();

    @Transient
    @Override
    public String getDisplay() {
        return code + " " + name;
    }
}
