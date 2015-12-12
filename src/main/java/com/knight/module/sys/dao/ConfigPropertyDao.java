package com.knight.module.sys.dao;

import com.knight.core.dao.jpa.BaseDao;
import com.knight.module.sys.entity.ConfigProperty;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;

/**
 * Date: 2015/11/28
 * Time: 22:23
 *
 * @author Rascal
 */
@Repository
public interface ConfigPropertyDao extends BaseDao<ConfigProperty, Long> {

    @QueryHints({@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_CACHEABLE, value = "true")})
    ConfigProperty findByPropKey(String propkey);
}
