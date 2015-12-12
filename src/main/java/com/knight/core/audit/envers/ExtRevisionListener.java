package com.knight.core.audit.envers;

import com.google.common.collect.Maps;
import com.knight.core.security.AuthContextHolder;
import com.knight.core.security.AuthUserDetails;
import org.hibernate.envers.RevisionListener;

import java.util.Map;

/**
 * 扩展默认的RevisionListener，额外追加记录登录用户信息
 * http://docs.jboss.org/hibernate/orm/4.2/devguide/en-US/html/ch15.html
 * <p>
 * Date: 2015/11/28
 * Time: 22:06
 *
 * @author Rascal
 */
public class ExtRevisionListener implements RevisionListener {

    /**
     * 以ThreadLocal机制把Web层相关审计属性值带入Envers监听器
     */
    private static final ThreadLocal<Map<String, String>> operationDataContainer = new ThreadLocal<Map<String, String>>();

    public static void setOperationExplain(String operationExplain) {
        Map<String, String> operationData = operationDataContainer.get();
        if (operationData == null) {
            operationData = Maps.newHashMap();
            operationDataContainer.set(operationData);
        }
        operationData.put("operationExplain", operationExplain);
    }

    public static void setControllerClassName(String controllerClassName) {
        Map<String, String> operationData = operationDataContainer.get();
        if (operationData == null) {
            operationData = Maps.newHashMap();
            operationDataContainer.set(operationData);
        }
        operationData.put("controllerClassName", controllerClassName);
    }

    public static void setControllerMethodName(String controllerMethodName) {
        Map<String, String> operationData = operationDataContainer.get();
        if (operationData == null) {
            operationData = Maps.newHashMap();
            operationDataContainer.set(operationData);
        }
        operationData.put("controllerMethodName", controllerMethodName);
    }

    @Override
    public void newRevision(Object revisionEntity) {
        ExtDefaultRevisionEntity revEntity = (ExtDefaultRevisionEntity) revisionEntity;
        AuthUserDetails authUserDetails = AuthContextHolder.getAuthUserDetails();
        if (authUserDetails != null) {
            revEntity.setAuthGuid(authUserDetails.getAuthGuid());
            revEntity.setAuthDisplay(authUserDetails.getAuthDisplay());
        }
        Map<String, String> operationData = operationDataContainer.get();
        if (operationData != null) {
            revEntity.setControllerClassName(operationData.get("controllerClassName"));
            revEntity.setControllerMethodName(operationData.get("controllerMethodName"));
        }
    }
}
