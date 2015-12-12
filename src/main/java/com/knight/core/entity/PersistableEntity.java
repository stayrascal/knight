package com.knight.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import com.knight.core.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Persistable;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Map;

import static com.knight.core.security.AuthContextHolder.getUserDisplay;

/**
 * Date: 2015/11/20
 * Time: 13:37
 *
 * @author Rascal
 */

/**
 * MappedSuperclass
 * 1、@MappedSuperclass 注解只能标准在类上
 * 2、标注为@MappedSuperclass的类将不是一个完整的实体类，将不会映射到数据库表，但其属性都将映射到其子类的数据库字段中
 * 3、标注为@MappedSuperclass的类不能再标注@Entity或@Table注解，也无需实现序列化接口。
 * 4、可以直接标注@EntityListeners实体监听器，作用范围仅在其所有继承类中，并且实体监听器同样可以保被其子类继承或重载。
 * 5、标注为@MappedSuperclass的类其属性最好设置为protected或default类型的，以保证其同一个包下的子类可以直接调用它的属性。便于实体监听器或带参数构造函数的操作。
 * 6、由于不是一个完整的实体类，因此其不能标注@Table，并且无法使用@UniqueConstraint设置字段的Unique属性，这一点以及对属性类型重载(如重载标注为@Lob的属性)的支持ＪＰＡ规范还有待改进。
 * 7、可以同时标注@DiscriminatorValue注解，以设定实体子类的实体标识字段的值。该属性一般是在实体继承的时候使用的较多，但是在实体映射的时候可以不用设置。
 */

/**
 * JsonInclude
 * 有时候，我们返回对象的字段可能会有为空的情况。缺省情况下，这些字段会以null的形式呈现出来。
 * 但如果我们希望忽略这些字段可以使用JsonInclude这个annotation。
 */
@MappedSuperclass
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class PersistableEntity<ID extends Serializable> implements Persistable<ID> {

    private static final long serialVersionUID = -7082170900881891109L;

    public static final String EXTRA_ATTRIBUTE_GRID_TREE_LEVEL = "level";

    /*在批量提交处理数据时，标识对象操作类型*/
    public static final String EXTRA_ATTRIBUTE_OPERATION = "operation";

    /*在显示或提交数据时，标识对象为脏数据需要处理*/
    public static final String EXTRA_ATTRIBUTE_DIRTY_ROW = "dirtyRow";

    /*Entity本身无用，主要用于UI层辅助参数传递*/
    @Transient/*@Transient表示该属性并非一个到数据库表的字段的映射,ORM框架将忽略该属性.*/
    private Map<String, Object> extraAttributes;

    /**
     * 用于快速判断对象是否新建状态
     */
    @Transient
    @JsonIgnore/*作用是json序列化时将该属性忽略掉，序列化和反序列化都受影响。*/
    public boolean isNew() {
        Serializable id = getId();
        return id == null || StringUtils.isBlank(String.valueOf(id));
    }

    /**
     * 用于快速判断对象是否编辑状态
     */
    @Transient
    @JsonIgnore
    public boolean isNotNew() {
        return !isNew();
    }


    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        Persistable that = (Persistable) obj;
        return null != this.getId() && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode += null == getId() ? 0 : getId().hashCode() * 31;
        return hashCode;
    }

    @Override
    public String toString() {
        return String.format("Entity of type %s with id: %s", this.getClass().getName(), getId());
    }

    @Transient
    public abstract String getDisplay();

    @Transient
    @JsonProperty/*把该属性的名称序列化为另外一个名称，如把trueName属性序列化为name，@JsonProperty("name")。*/
    public Map<String, Object> getExtraAttributes() {
        return extraAttributes;
    }

    @Transient
    public void setExtraAttributes(Map<String, Object> extraAttributes) {
        this.extraAttributes = extraAttributes;
    }

    @Transient
    public void addExtraAttribute(String key, Object value) {
        if (extraAttributes == null) {
            extraAttributes = Maps.newHashMap();
        }
        extraAttributes.put(key, value);
    }

    /**
     * 从拓展属性中取值判断当前对象是否标记需要删除
     * 一般用于前端UI对关联集合对象元素移除操作
     */
    @Transient
    @JsonIgnore
    public boolean isMarkedRemove() {
        if (extraAttributes == null) {
            return false;
        }
        Object opParams = extraAttributes.get(EXTRA_ATTRIBUTE_OPERATION);
        if (opParams == null) {
            return false;
        }
        String op = null;
        if (opParams instanceof String[]) {
            op = ((String[]) opParams)[0];
        } else if (opParams instanceof String) {
            op = (String) opParams;
        }
        return "remove".equalsIgnoreCase(op);
    }

    @Transient
    @JsonIgnore
    public String getExtraAttributesValue(String key) {
        if (extraAttributes == null) {
            return null;
        }
        Object opParams = extraAttributes.get(key);
        if (opParams == null) {
            return null;
        }
        String op = null;
        if (opParams instanceof String[]) {
            op = ((String[]) opParams)[0];
        } else if (opParams instanceof String) {
            op = (String) opParams;
        }
        return op;
    }

    /**
     * 用于辅助构建最近操作说明
     *
     * @param lastOperation 如”审核“
     * @return 追加了登陆用户/操作时间等信息的操作说明
     */
    @Transient
    @JsonIgnore
    public String buildLastOperationSummary(String lastOperation) {
        return getUserDisplay() + lastOperation + ":" + DateUtils.formatTimeNow();
    }
}
