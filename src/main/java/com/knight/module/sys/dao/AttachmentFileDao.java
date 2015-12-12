package com.knight.module.sys.dao;

import com.knight.core.dao.jpa.BaseDao;
import com.knight.module.sys.entity.AttachmentFile;
import org.springframework.stereotype.Repository;

/**
 * Date: 2015/11/29
 * Time: 0:05
 *
 * @author Rascal
 */
@Repository
public interface AttachmentFileDao extends BaseDao<AttachmentFile, String> {
}
