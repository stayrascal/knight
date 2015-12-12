package com.knight.core.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 拓展标准的PropertyPlaceholderConfigurer把属性文件中的配置参数放入到全局Map变量，便于其他接口访问key-value配置数据
 *
 * @author Rascal
 * Date: 2015/11/18
 * Time: 15:50
 */
public class ExtPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    private static Logger logger = LoggerFactory.getLogger(ExtPropertyPlaceholderConfigurer.class);

    private static Map<String, String> ctxPropertiesMap;

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        super.processProperties(beanFactoryToProcess, props);
        ctxPropertiesMap = new HashMap<String, String>();
        logger.info("Putting PropertyPlaceHolder {} datas into cache...", props.size());
        for (Object key : props.keySet()){
            String keyStr = key.toString();
            String value = props.getProperty(keyStr);
            ctxPropertiesMap.put(keyStr, value);
        }
    }

    public String getProperty(String name){
        return ctxPropertiesMap.get(name);
    }
}
