package com.knight.support.service;

import com.knight.core.context.ExtPropertyPlaceholderConfigurer;
import com.knight.module.sys.entity.ConfigProperty;
import com.knight.module.sys.service.ConfigPropertyService;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 基于数据库加载动态配置参数
 * 框架拓展属性加载：Spring除了从.properties加载属性数据
 * 并且数据库如果存在同名属性则优先取数据库的属性值覆盖配置文件的值
 * 为了避免意外的数据库配置导致系统崩溃，约定以cfg打头标识的参数表示可以被数据库参数覆写，其余的则不会覆盖文件定义的属性值
 * Date: 2015/11/26
 * Time: 22:07
 *
 * @author Rascal
 */
@Component
public class DynamicConfigService {

    private final static Logger logger = LoggerFactory.getLogger(DynamicConfigService.class);

    @Autowired(required = false)
    private ExtPropertyPlaceholderConfigurer extPropertyPlaceholderConfigurer;

    @Autowired
    private ConfigPropertyService configPropertyService;

    /**
     * 根据key获取对应动态参数值
     */
    public String getString(String key) {
        return getString(key, null);
    }

    /**
     * 根据key获取对应动态参数值， 如果没有则返回defaultValue
     */
    public String getString(String key, String defaultValue) {
        String val = null;
        //cfg打头参数，首先从数据库取值
        if (key.startsWith("cfg")) {
            ConfigProperty cfg = configPropertyService.findByPropKey(key);
            if (cfg != null) {
                val = cfg.getSimpleValue();
            }
        }
        //未取到则继续从Spring属性文件定义取
        if (val == null) {
            if (extPropertyPlaceholderConfigurer != null) {
                val = extPropertyPlaceholderConfigurer.getProperty(key);
            } else {
                logger.warn("当前不是以ExtPropertyPlaceholderConfigurer扩展模式定义，因此无法加载获取Spring属性配置");
            }
        }
        if (val == null) {
            logger.warn("Undefined config property for: {}", key);
            return defaultValue;
        } else {
            return val.trim();
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return BooleanUtils.toBoolean(getString(key, String.valueOf(defaultValue)));
    }
}
