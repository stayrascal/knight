package com.knight.core.audit;

import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.util.Date;

/**
 * Date: 2015/11/20
 * Time: 16:57
 *
 * @author Rascal
 */
public interface DefaultAuditable<U, ID extends Serializable> extends Persistable<ID> {

    /**
     * Returns the user who created this entity.
     *
     * @return the createdBy
     */
    U getCreatedBy();

    /**
     * Sets the user who created this entity
     *
     * @param createdBy createdBy the creation entity to set
     */
    void setCreatedBy(final U createdBy);

    /**
     * Returns the creation date of the entity.
     *
     * @return the createDate
     */
    Date getCreatedDate();

    /**
     * Sets the creation date of the entity.
     *
     * @param createdDate creationDate the creation date to set
     */
    void setCreatedDate(final Date createdDate);

    /**
     * Returns the user who modified the entity lastly.
     *
     * @return the lastModifiedBy
     */
    U getLastModifiedBy();

    /**
     * Sets the user who modified the entity lastly.
     *
     * @param lastModifiedBy lastModifiedBy the last modifying entity to set
     */
    void setLastModifiedBy(final U lastModifiedBy);

    /**
     * Returns the date of the last modification
     *
     * @return the lastModifiedDate
     */
    Date getLastModifiedDate();

    /**
     * Sets the date of the last modification.
     *
     * @param lastModifiedDate lastModifiedDate the date of the last modification to set
     */
    void setLastModifiedDate(final Date lastModifiedDate);
}
