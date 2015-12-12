package com.knight.core.audit;

import com.knight.core.security.AuthContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

/**
 * 审计记录创建和修改信息
 * <p>
 * Date: 2015/11/21
 * Time: 23:05
 *
 * @author Rascal
 */
@Component
public class SaveUpdateAuditListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private boolean dateTimeForNow = true;

    //考虑到效率影响和实际作用不大，默认关闭update更新记录处理
    private boolean modifyOnceCreation = false;

    //如果需要记录变更过程，可以考虑使用专门的hibernate envers机制
    private boolean skipUpdateAudit = true;

    public void setDateTimeForNow(boolean dateTimeForNow) {
        this.dateTimeForNow = dateTimeForNow;
    }

    public void setModifyOnceCreation(boolean modifyOnceCreation) {
        this.modifyOnceCreation = modifyOnceCreation;
    }

    /**
     * Sets modification abd creation date and auditor on the target object in
     * case it implement DefaultAuditable on persist events.
     */
    @PrePersist
    public void touchForCreate(Object target) {
        touch(target, false);
    }

    /**
     * Sets modification and creation date and auditor on the target object in
     * case it implements DefaultAuditable on uodate events
     */
    @PreUpdate
    public void touchForUpdate(Object target) {
        if (skipUpdateAudit) {
            return;
        }
        touch(target, false);
    }

    private void touch(Object target, boolean isNew) {
        if (!(target instanceof DefaultAuditable)) {
            return;
        }

        DefaultAuditable<String, ?> auditable = (DefaultAuditable<String, ?>) target;

        String auditor = touchAuditor(auditable, isNew);
        Date now = dateTimeForNow ? touchDate(auditable, isNew) : null;

        Object defaultedNow = now == null ? "Not Set" : now;
        Object defaultedAuditor = auditor == null ? "unknown" : auditor;

        logger.trace("Touch {} - Last modification at {} by {}", new Object[]{auditable, defaultedNow, defaultedAuditor});
    }

    /**
     * Touches the auditable regarding modification and creation date. Creation date is only set on new auditables.
     */
    private Date touchDate(DefaultAuditable<String, ?> auditable, boolean isNew) {
        Date now = new Date();
        if (isNew) {
            auditable.setCreatedDate(now);
            if (!modifyOnceCreation) {
                return now;
            }
        }
        auditable.setLastModifiedDate(now);
        return now;
    }

    /**
     * Sets modifying and creating auditioner. Creating auditioner is only set on new auditables.
     */
    private String touchAuditor(DefaultAuditable<String, ?> auditable, boolean isNew) {
        String auditor = AuthContextHolder.getUserDisplay();
        if (isNew) {
            auditable.setCreatedBy(auditor);
            if (!modifyOnceCreation) {
                return auditor;
            }
        }
        auditable.setLastModifiedBy(auditor);
        return auditor;
    }
}
