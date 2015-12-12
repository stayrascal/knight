package com.knight.module.sys.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.knight.core.annotation.MetaData;
import com.knight.core.entity.BaseNativeEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Date: 2015/11/28
 * Time: 22:18
 *
 * @author Rascal
 */
@Getter
@Setter
@Accessors(chain = true)
@Access(AccessType.FIELD)
@Entity
@Table(name = "sys_configproperty", schema = "", catalog = "knight")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@MetaData(value = "配置属性")
@Audited
public class ConfigProperty extends BaseNativeEntity {

    private static final long serialVersionUID = -5682018193396399434L;

    @MetaData(value = "代码")
    @Column(length = 64, unique = true, nullable = false)
    private String propKey;

    @MetaData(value = "名称")
    @Column(length = 256, nullable = false)
    private String propName;

    @MetaData(value = "简单属性值")
    @Column(length = 256)
    private String simpleValue;

    @MetaData(value = "HTML属性值")
    @Lob
    @JsonIgnore
    private String htmlValue;

    @MetaData(value = "参数属性用法说明")
    @Column(length = 2000)
    private String propDescn;

    @Override
    @Transient
    public String getDisplay() {
        return propKey;
    }
}
