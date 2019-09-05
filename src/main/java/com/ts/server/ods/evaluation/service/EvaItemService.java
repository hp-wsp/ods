package com.ts.server.ods.evaluation.service;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.common.id.IdGenerators;
import com.ts.server.ods.etask.dao.DeclarationDao;
import com.ts.server.ods.etask.dao.TaskItemDao;
import com.ts.server.ods.evaluation.dao.EvaItemDao;
import com.ts.server.ods.evaluation.domain.EvaItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评比指标业务服务
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Service
@Transactional(readOnly = true)
public class EvaItemService {

    private final EvaItemDao dao;
    private final TaskItemDao taskItemDao;
    private final DeclarationDao declarationDao;
    private final EvaluationService evaluationService;

    @Autowired
    public EvaItemService(EvaItemDao dao, TaskItemDao taskItemDao,
                          DeclarationDao declarationDao, EvaluationService evaluationService) {

        this.dao = dao;
        this.taskItemDao = taskItemDao;
        this.declarationDao = declarationDao;
        this.evaluationService = evaluationService;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public EvaItem save(EvaItem t) {
        evaluationService.get(t.getEvaId());

        t.setNum(buildNum(t.getNum()));
        if (dao.hasNumber(t.getEvaId(), t.getNum())) {
            throw new BaseException("编号已经存在");
        }

        t.setId(IdGenerators.uuid());
        dao.insert(t);

        return dao.findOne(t.getId());
    }

    private String buildNum(String num){
        num = StringUtils.replaceChars(num, '）', ')');
        String[] array = StringUtils.split(num, "-");
        for(int i =0; i < array.length; i++){
            array[i] = StringUtils.trim(array[i]);
        }
        return StringUtils.join(array, "-");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public EvaItem importItem(EvaItem t){
        t.setNum(buildNum(t.getNum()));
        if(!dao.hasNumber(t.getEvaId(), t.getNum())){
            t.setId(IdGenerators.uuid());
            dao.insert(t);
            return get(t.getId());
        }

        return dao.findOneByNum(t.getEvaId(), t.getNum());
    }

    public EvaItem get(String id){
        try{
            return dao.findOne(id);
        }catch (DataAccessException e){
            throw new BaseException("指标不存在");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public EvaItem update(EvaItem t){
        EvaItem o = get(t.getId());

        t.setEvaId(o.getEvaId());
        if(!dao.update(t)){
             throw new BaseException("修改指标不存在");
        }

        return get(t.getId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean delete(String id){
        if(declarationDao.hasByEvaItemId(id)){
            throw new BaseException("指标已经申报，不能删除");
        }
        boolean ok = dao.delete(id);
        if(ok){
            taskItemDao.deleteByEvaId(id);
        }
        return ok;
    }

    public Long count(String evaId, String num, String require){
        return dao.count(evaId, num, require);
    }

    public List<EvaItem> query(String evaId, String num, String require, int offset, int limit){
        return dao.find(evaId, num, require, offset, limit);
    }
}
