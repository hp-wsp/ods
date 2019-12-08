package com.ts.server.ods.evaluation.service;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.common.id.IdGenerators;
import com.ts.server.ods.evaluation.dao.EvaItemDao;
import com.ts.server.ods.evaluation.dao.EvaluationDao;
import com.ts.server.ods.evaluation.domain.Evaluation;
import com.ts.server.ods.evaluation.event.EvaluationEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 测评业务服务
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Service
@Transactional(readOnly = true)
public class EvaluationService {

    private final EvaluationDao dao;
    private final EvaItemDao itemDao;
    private final ImportEvaluationService importService;
    private final ApplicationEventPublisher publisher;

    @Autowired
    public EvaluationService(EvaluationDao dao, EvaItemDao itemDao,
                             ImportEvaluationService importService, ApplicationEventPublisher publisher) {
        this.dao = dao;
        this.itemDao = itemDao;
        this.importService = importService;
        this.publisher = publisher;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Evaluation save(Evaluation t, String importId){

        t.setId(IdGenerators.uuid());
        t.setStatus(Evaluation.Status.WAIT);
        dao.insert(t);

        if(StringUtils.isNotBlank(importId)){
            if(!dao.has(importId)){
                throw new BaseException("导入评测不存在");
            }
            importService.importEvaluation(t, importId);
        }

        return dao.findOne(t.getId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Evaluation update(Evaluation t){
        Evaluation o = get(t.getId());

        if(!StringUtils.equals(o.getName(), t.getName())){
            notifyEvent(t.getId(), "update");
        }

        if(!dao.update(t)){
            throw new BaseException("修改测评失败");
        }

        return dao.findOne(t.getId());
    }

    private void notifyEvent(String id, String event){
        publisher.publishEvent(new EvaluationEvent(id, event));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Evaluation updateStatus(String id, Evaluation.Status status){
        if(!dao.updateStatus(id, status)){
            throw new BaseException("修改状态失败");
        }

        if(status == Evaluation.Status.CLOSE){
            dao.updateOpenDec(id, false);
        }

        notifyEvent(id, "updateStatus");

        return get(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean updateExport(String id, String exportId){
        return dao.updateExport(id, exportId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void sendSms(String id){
        dao.updateSms(id, true);
    }

    public Evaluation get(String id){
        try{
            return dao.findOne(id);
        }catch (DataAccessException e){
            throw new BaseException("得到测评失败");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean delete(String id){
        Evaluation t = get(id);
        if(t.getStatus() == Evaluation.Status.OPEN){
            throw new BaseException("评测已经开启不能删除");
        }

        if(!dao.delete(id)){
            throw new BaseException("删除评测失败");
        }

        itemDao.deleteByEvaId(id);

        notifyEvent(id, "delete");

        return true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Evaluation openDec(String id){
        Evaluation t = get(id);

        if(t.isOpenDec()){
            throw new BaseException("已经开启申报");
        }

        if(!dao.updateOpenDec(id, true)){
            throw new BaseException("开启测评申报失败");
        }

        notifyEvent(id, "openDec");

        return get(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Evaluation closeDec(String id){
        Evaluation t = get(id);

        if(!t.isOpenDec()){
            throw new BaseException("已经关闭申报");
        }

        if(!dao.updateOpenDec(id, false)){
            throw new BaseException("关闭申报失败");
        }

        notifyEvent(id, "closeDec");

        return get(id);
    }

    public Long count(String name, Evaluation.Status status){
        return dao.count(name, status);
    }

    public List<Evaluation> query(String name, Evaluation.Status status, int offset, int limit){
        return dao.find(name, status, offset, limit);
    }

    public List<Evaluation> queryActive(){
        return dao.findActive();
    }

    public Optional<Evaluation> queryLasted(){
        return dao.findLasted();
    }

}
