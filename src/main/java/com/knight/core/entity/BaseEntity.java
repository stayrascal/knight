package com.knight.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.knight.core.audit.DefaultAuditable;
import com.knight.core.audit.SaveUpdateAuditListener;
import com.knight.core.web.json.DateTimeJsonSerializer;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.springframework.data.annotation.Version;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Date: 2015/11/20
 * Time: 13:36
 *
 * @author Rascal
 */
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "javassistLazyInitializer", "revisionEntity", "handler"}, ignoreUnknown = true)
@MappedSuperclass
@EntityListeners({SaveUpdateAuditListener.class})
@AuditOverrides({@AuditOverride(forClass = BaseEntity.class)})
public abstract class BaseEntity<ID extends Serializable> extends PersistableEntity<ID> implements DefaultAuditable<String, ID> {

    private static final long serialVersionUID = 4345757327557599254L;

    /*乐观锁版本，初始设置为0*/
    private Integer version = 0;

    private String createdBy;

    private Date createdDate;

    private String lastModifiedBy;

    private Date lastModifiedDate;

    public abstract void setId(final ID id);

    @Version
    @Column(name = "optlock", nullable = false)
    @JsonProperty
    @JsonView(JsonViews.Admin.class)
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void resetCommonProperties() {
        setId(null);
        version = 0;
        addExtraAttribute(PersistableEntity.EXTRA_ATTRIBUTE_DIRTY_ROW, true);
    }

    private static final String[] PROPERTY_LIST = new String[]{"id", "version"};

    public String[] retriveCommonProperties() {
        return PROPERTY_LIST;
    }

    @Override
    @Transient
    @JsonProperty
    @JsonView(JsonViews.Admin.class)/*控制输入输出后的json.*/
    public String getDisplay() {
        return String.format("[%s]%s", getId(), this.getClass().getSimpleName());
    }

    @Column(length = 100)
    @JsonIgnore
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Column(length = 100)
    @JsonIgnore
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @JsonSerialize(using = DateTimeJsonSerializer.class)
    @JsonIgnore
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}