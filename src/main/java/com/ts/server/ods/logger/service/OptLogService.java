package com.ts.server.ods.logger.service;

import com.ts.server.ods.logger.dao.OptLogDao;
import com.ts.server.ods.logger.domain.OptLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 日志操作业务服务
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Service
@Transactional(readOnly = true)
public class OptLogService {
    private final OptLogDao dao;

    @Autowired
    public OptLogService(OptLogDao dao) {
        this.dao = dao;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void save(String detail, String username){
        save(detail, new String[0], new Object[0], username);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void save(String detail, String[] paramKeys, Object[] paramValues, String username){
        OptLog t = new OptLog();

        t.setDetail(detail);
        t.setParams(buildParams(paramKeys, paramValues));
        t.setUsername(username);

        dao.insert(t);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void save(String name, String username, String detail){
        OptLog t = new OptLog();

        t.setDetail(name);
        t.setUsername(username);
        t.setParams(detail);

        dao.insert(t);
    }

    private String buildParams(String[] paramKeys, Object[] paramValues){
        int len = paramKeys.length;
        StringBuilder builder = new StringBuilder(50);
        for(int i = 0; i < len; i++){
            builder.append(paramKeys[i]).append(":").append(paramValues[i]== null? "": paramValues[i]).append(" ");
        }

        return builder.toString();
    }

    public Long count(String detail, String params, String username, Date fromDate, Date toDate){
        return dao.count(detail, params, username, fromDate, toDate);
    }

    public List<OptLog> query(String detail, String params, String username, Date fromDate, Date toDate, int offset, int limit){
        return dao.find(detail, params, username, fromDate, toDate, offset, limit);
    }
}
