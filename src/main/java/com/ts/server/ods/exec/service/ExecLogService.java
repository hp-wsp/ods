package com.ts.server.ods.exec.service;

import com.ts.server.ods.common.id.IdGenerators;
import com.ts.server.ods.exec.dao.ExecLogDao;
import com.ts.server.ods.exec.domain.ExecLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 导出测评任务服务
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Service
@Transactional(readOnly = true)
public class ExecLogService {

    private final ExecLogDao dao;

    @Autowired
    public ExecLogService(ExecLogDao dao) {
        this.dao = dao;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ExecLog createTask(String key, String remark){
        String id = IdGenerators.uuid();
        dao.insert(id, key, remark);
        return dao.findOne(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean success(String id){
        return dao.success(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean fail(String id, String errMsg){
        return dao.fail(id, errMsg);
    }

    public List<ExecLog> query(String taskKey){
        return dao.find(taskKey);
    }
}
