package com.ts.server.ods.base.service;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.base.dao.GradeRateDao;
import com.ts.server.ods.base.domain.GradeRate;
import com.ts.server.ods.common.id.IdGenerators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评分比例业务服务
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Service
@Transactional(readOnly = true)
public class GradeRateService {
    private final GradeRateDao dao;

    @Autowired
    public GradeRateService(GradeRateDao dao) {
        this.dao = dao;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public GradeRate save(GradeRate t){
        if(dao.hasLevel(t.getLevel())){
            throw new BaseException("评分比例已经存在");
        }

        t.setId(IdGenerators.uuid());
        dao.insert(t);

        return dao.findOne(t.getId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public GradeRate update(GradeRate t){

        if(!dao.update(t)){
            throw new BaseException("修补评分比例失败");
        }

        return dao.findOne(t.getId());
    }

    public GradeRate get(String id){
        try{
            return dao.findOne(id);
        }catch (DataAccessException e){
            throw new BaseException("评分比例不存在");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean delete(String id){
        return dao.delete(id);
    }

    public List<GradeRate> queryAll(){
        return dao.findAll();
    }

    public Long count(String level){
        return dao.count(level);
    }

    public List<GradeRate> query(String level, int offset, int limit){
        return dao.find(level, offset, limit);
    }
}
