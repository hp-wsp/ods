package com.ts.server.ods.etask.service;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.base.domain.GradeRate;
import com.ts.server.ods.base.service.GradeRateService;
import com.ts.server.ods.common.id.IdGenerators;
import com.ts.server.ods.etask.dao.DeclarationDao;
import com.ts.server.ods.etask.dao.TaskItemDao;
import com.ts.server.ods.etask.domain.TaskCard;
import com.ts.server.ods.etask.domain.TaskItem;
import com.ts.server.ods.evaluation.domain.EvaItem;
import com.ts.server.ods.evaluation.service.EvaItemService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评测卡指标业务服务
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Service
@Transactional(readOnly = true)
public class TaskItemService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskItemService.class);

    private final TaskItemDao dao;
    private final DeclarationDao declarationDao;
    private final TaskCardService cardService;
    private final EvaItemService itemService;
    private final GradeRateService rateService;

    @Autowired
    public TaskItemService(TaskItemDao dao, DeclarationDao declarationDao,
                           TaskCardService cardService, EvaItemService itemService,
                           GradeRateService rateService) {

        this.dao = dao;
        this.declarationDao = declarationDao;
        this.cardService = cardService;
        this.itemService = itemService;
        this.rateService = rateService;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TaskItem save(TaskItem t){
        cardService.get(t.getCardId());

        EvaItem item = itemService.get(t.getEvaItemId());
        String num = item.getNum();
        t.setEvaNum(num);

        t.setId(IdGenerators.uuid());
        fillScore(t, gradeRates());
        dao.insert(t);

        cardService.updateScore(t.getCardId());
        cardService.updateItemCount(t.getCardId());

        return dao.findOne(t.getId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void importItem(TaskCard card, TaskItem t){

        t.setEvaNum(buildNum(t.getEvaNum()));
        EvaItem evaItem = importEvaItem(card.getEvaId(), t);
        t.setEvaItemId(evaItem.getId());
        t.setCardId(card.getId());
        t.setId(IdGenerators.uuid());

        dao.insert(t);
    }

    private String buildNum(String num){
        num = StringUtils.replaceChars(num, '）', ')');
        String[] array = StringUtils.split(num, "-");
        for(int i =0; i < array.length; i++){
            array[i] = StringUtils.trim(array[i]);
        }
        return StringUtils.join(array, "-");
    }

    private EvaItem importEvaItem(String evaId, TaskItem t){
        EvaItem item = new EvaItem();
        item.setEvaId(evaId);
        item.setNum(t.getEvaNum());
        item.setRequire(t.getRequireContent());
        item.setGrade(t.getGradeContent());
        item.setRemark(t.getRemark());
        item.setResults(t.getResults().stream().map(TaskItem.TaskItemResult::getLevel).toArray(String[]::new));
        return itemService.importItem(item);
    }

    private Map<String, Integer> gradeRates(){
        return rateService.queryAll().stream().collect(Collectors.groupingBy(GradeRate::getLevel))
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0).getRate()));
    }

    private void fillScore(TaskItem t, Map<String, Integer> rates){
        int score = t.getScore();

        for(TaskItem.TaskItemResult result: t.getResults()){
            int rate;
            if(!rates.containsKey(result.getLevel())){
                LOGGER.warn("Fill score not rate level={}", result.getLevel());
                rate = 0;
            }else{
                rate = rates.get(result.getLevel());
            }
            result.setScore(score * rate / 100);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TaskItem update(TaskItem t){
        TaskItem o = get(t.getId());

        EvaItem item = itemService.get(t.getEvaItemId());

        String num = item.getNum();
        t.setEvaNum(num);
        fillScore(t, gradeRates());
        t.setShowOrder(o.getShowOrder());

        if(!dao.update(t)){
            cardService.updateScore(o.getCardId());
            throw new BaseException("修改指标失败");
        }

        return dao.findOne(t.getId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteByCardId(String cardId){
        dao.deleteByCardId(cardId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean delete(String id){

        if(declarationDao.hasByItemId(id)){
            throw new BaseException("测评任务指标已经申报不能删除");
        }

        TaskItem o = get(id);
        boolean ok =  dao.delete(id);
        if(ok){
            cardService.updateScore(o.getCardId());
            cardService.updateItemCount(o.getCardId());
        }

        return ok;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean updateDeclare(String id, boolean isDeclare){
        boolean ok =  dao.updateDeclare(id, isDeclare);
        if(ok){
            TaskItem t = get(id);
            cardService.updateDecCount(t.getCardId());
        }
        return ok;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TaskItem grade(String id, String level, int score, String remark, String assId){
        TaskItem item = get(id);
        TaskCard card = cardService.get(item.getCardId());

        if(!card.isOpen()){
            throw  new BaseException("评测已经关闭");
        }

        if(card.getStatus() == TaskCard.Status.WAIT){
            throw new BaseException("评测还未提交");
        }

        if(card.getStatus() == TaskCard.Status.BACK){
            throw new BaseException("评测已经退回");
        }

        if(!StringUtils.equals(card.getAssId(), assId)){
            throw new BaseException("无权限打分");
        }

        if(!dao.grade(id, level, score, remark)){
            throw new BaseException("打分失败");
        }

        cardService.updateGradeScore(card.getId());

        return get(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void clearGrade(String id){
        dao.grade(id, "", 0,"");
        TaskItem item = get(id);
        cardService.updateGradeScore(item.getCardId());
    }

    public TaskItem get(String id){
        try{
            return dao.findOne(id);
        }catch (DataAccessException e){
            throw new BaseException("指标不存在");
        }
    }

    public List<TaskItem> queryByCardId(String cardI){
        return dao.findByCardId(cardI);
    }

    public Long count(String taskId, String num, String require, String grade){
        return dao.count(taskId, num, require, grade);
    }

    public List<TaskItem> query(String taskId, String num, String require, String grade, int offset, int limit){
        return dao.find(taskId, num, require, grade, offset, limit);
    }
}
