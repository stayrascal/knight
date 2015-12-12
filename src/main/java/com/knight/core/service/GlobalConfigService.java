package com.knight.core.service;

import com.knight.core.annotation.MetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Date: 2015/11/25
 * Time: 15:11
 *
 * @author Rascal
 */
@Component
public class GlobalConfigService {

    private static final Logger logger = LoggerFactory.getLogger(GlobalConfigService.class);

    @MetaData(value = "开发模式", comments = "更宽松的权限控制, 更多的日志信息。详见application.properties配置参数定义")
    private static boolean devMode  = false;

    @MetaData(value = "演示模式", comments = "对演示环境进行特殊控制以避免不必要的随意数据修改导致系统混乱")
    private static boolean demoMode = false;

    @MetaData(value = "构建版本")
    private static String buildVersion;

    public static boolean isDevMode(){
        return devMode;
    }

    public static boolean isDemoMode(){
        return demoMode;
    }

    @Value("${dev_mode:false}")
    public static void setDevMode(boolean devMode) {
        GlobalConfigService.devMode = devMode;
        logger.info("System running at dev_mode={}", GlobalConfigService.devMode);
    }

    @Value("${demo_dev:false}")
    public static void setDemoMode(boolean demoMode) {
        GlobalConfigService.demoMode = demoMode;
        logger.info("System running at demo_mode={}", GlobalConfigService.demoMode);
    }


    public static String getBuildVersion() {
        return buildVersion;
    }

    @Value("${build_version}")
    public static void setBuildVersion(String buildVersion) {
        GlobalConfigService.buildVersion = buildVersion;
        logger.info("System running at build_version={}", GlobalConfigService.buildVersion);
    }
}
