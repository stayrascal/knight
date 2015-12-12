package com.knight.module.sys.service;

import com.knight.core.dao.jpa.BaseDao;
import com.knight.core.service.BaseService;
import com.knight.module.sys.dao.AttachmentFileDao;
import com.knight.module.sys.entity.AttachmentFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Date: 2015/11/29
 * Time: 0:05
 *
 * @author Rascal
 */
@Service
@Transactional
public class AttachmentFileService extends BaseService<AttachmentFile, String> {

    @Autowired
    private AttachmentFileDao attachmentFileDao;

    @Override
    protected BaseDao<AttachmentFile, String> getEntityDao() {
        return attachmentFileDao;
    }
}
