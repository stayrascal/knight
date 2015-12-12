package com.knight.module.sys.service;

import com.knight.core.dao.jpa.BaseDao;
import com.knight.core.service.BaseService;
import com.knight.module.sys.dao.ConfigPropertyDao;
import com.knight.module.sys.entity.ConfigProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Date: 2015/11/26
 * Time: 23:34
 *
 * @author Rascal
 */
@Service
@Transactional
public class ConfigPropertyService extends BaseService<ConfigProperty, Long> {

    @Autowired
    private ConfigPropertyDao configPropertyDao;

    protected BaseDao<ConfigProperty, Long> getEntityDao() {
        return configPropertyDao;
    }

    @Transactional(readOnly = true)
    public ConfigProperty findByPropKey(String propKey) {
        return configPropertyDao.findByPropKey(propKey);
    }

    @Transactional(readOnly = true)
    public String findValueByPropKey(String propKey) {
        ConfigProperty configProperty = configPropertyDao.findByPropKey(propKey);
        if (configProperty != null) {
            return configProperty.getSimpleValue();
        }
        return null;
    }
}
