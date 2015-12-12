package com.knight.module.auth.entity;

import com.knight.core.cons.GlobalConstant;
import com.knight.core.entity.BaseNativeEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * Date: 2015/11/22
 * Time: 23:38
 *
 * @author Rascal
 */
@Setter
@Getter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "auth_roler2privilege", schema = "", catalog = "knight", uniqueConstraints = @UniqueConstraint(columnNames = {"privilege_id", "role_id"}))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RoleR2Privilege extends BaseNativeEntity {

    private static final long serialVersionUID = -7075497481859250155L;

    /**
     * 关联权限对象
     */
    @ManyToOne
    @JoinColumn(name = "privilege_id", nullable = false, foreignKey = @ForeignKey(name = GlobalConstant.GlobalForeignKeyName))
    private Privilege privilege;

    /**
     * 关联角色对象
     */
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(name = GlobalConstant.GlobalForeignKeyName))
    private Role role;

    @Transient
    @Override
    public String getDisplay() {
        return privilege.getDisplay() + "_" + role.getDisplay();
    }

}
