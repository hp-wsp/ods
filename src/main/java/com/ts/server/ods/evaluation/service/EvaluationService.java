package com.ts.server.ods.evaluation.service;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.base.dao.CompanyDao;
import com.ts.server.ods.base.dao.GradeRateDao;
import com.ts.server.ods.base.domain.Company;
import com.ts.server.ods.base.domain.GradeRate;
import com.ts.server.ods.common.id.IdGenerators;
import com.ts.server.ods.etask.dao.DeclarationDao;
import com.ts.server.ods.etask.dao.TaskCardDao;
import com.ts.server.ods.etask.dao.TaskItemDao;
import com.ts.server.ods.etask.domain.TaskCard;
import com.ts.server.ods.etask.domain.TaskItem;
import com.ts.server.ods.evaluation.dao.EvaItemDao;
import com.ts.server.ods.evaluation.dao.EvaluationDao;
import com.ts.server.ods.evaluation.dao.EvaluationLogDao;
import com.ts.server.ods.evaluation.domain.EvaItem;
import com.ts.server.ods.evaluation.domain.Evaluation;
import com.ts.server.ods.evaluation.domain.EvaluationLog;
import com.ts.server.ods.evaluation.service.event.EvaCloseDecEvent;
import com.ts.server.ods.evaluation.service.event.EvaCloseGradeEvent;
import com.ts.server.ods.evaluation.service.event.EvaOpenDecEvent;
import com.ts.server.ods.evaluation.service.event.EvaOpenGradeEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 测评业务服务
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Service
@Transactional(readOnly = true)
public class EvaluationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EvaluationService.class);

    private final EvaluationDao dao;
    private final EvaItemDao itemDao;
    private final TaskCardDao taskCardDao;
    private final TaskItemDao taskItemDao;
    private final DeclarationDao declarationDao;
    private final EvaluationLogDao logDao;
    private final CompanyDao companyDao;
    private final GradeRateDao gradeRateDao;
    private final ApplicationEventPublisher publisher;

    @Autowired
    public EvaluationService(EvaluationDao dao, EvaItemDao itemDao, TaskCardDao taskCardDao,
                             TaskItemDao taskItemDao, DeclarationDao declarationDao, EvaluationLogDao logDao,
                             CompanyDao companyDao, GradeRateDao gradeRateDao, ApplicationEventPublisher publisher) {
        this.dao = dao;
        this.itemDao = itemDao;
        this.taskCardDao = taskCardDao;
        this.taskItemDao = taskItemDao;
        this.declarationDao = declarationDao;
        this.logDao = logDao;
        this.companyDao = companyDao;
        this.gradeRateDao = gradeRateDao;
        this.publisher = publisher;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Evaluation save(Evaluation t, String importId, boolean isImportTask, String username){

        t.setId(IdGenerators.uuid());
        t.setStatus(Evaluation.Status.WAIT);
        dao.insert(t);

        if(StringUtils.isNotBlank(importId)){
            importItem(t, importId, isImportTask);
        }

        saveLog(t.getId(), "创建测评", username);

        return dao.findOne(t.getId());
    }

    private void saveLog(String evaId, String detail, String username){
        EvaluationLog t  = new EvaluationLog();

        t.setId(IdGenerators.uuid());
        t.setEvaId(evaId);
        t.setDetail(detail);
        t.setUsername(username);
        t.setDay(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));

        logDao.insert(t);
    }

    private void importItem(Evaluation evaluation, String importId, boolean isImportTask){

        try{
            get(importId);
        }catch (BaseException e){
            throw new BaseException("导入评测不存在");
        }

        ImportHistory importHistory = new ImportHistory(itemDao, taskCardDao, taskItemDao, companyDao, gradeRateDao);
        importHistory.importEvaluation(evaluation, importId, isImportTask);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Evaluation update(Evaluation t){

        if(!dao.update(t)){
            throw new BaseException("修改测评失败");
        }

        return dao.findOne(t.getId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Evaluation updateStatus(String id, Evaluation.Status status, String username){
        if(!dao.updateStatus(id, status)){
            throw new BaseException("修改状态失败");
        }

        if(status == Evaluation.Status.OPEN) {
            publisher.publishEvent(new EvaOpenGradeEvent(id));
            saveLog(id, "开启评测", username);
            return get(id);
        }

        Evaluation t = get(id);
        if(t.isOpenDec()){
            closeDec(id, username);
        }
        publisher.publishEvent(new EvaCloseGradeEvent(id));
        saveLog(id, "关闭评测", username);
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

        if(declarationDao.hasByEvaId(id)){
            throw new BaseException("评测已经申报不能删除");
        }

        Evaluation t = get(id);
        if(t.getStatus() == Evaluation.Status.OPEN){
            throw new BaseException("评测已经开启不能删除");
        }

        if(!dao.delete(id)){
            throw new BaseException("删除评测失败");
        }

        if(taskCardDao.hasByEvaId(id)){
            throw new BaseException("已经分配测评任务不能删除");
        }

        itemDao.deleteByEvaId(id);

        return true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Evaluation openDec(String id, String username){
        Evaluation t = get(id);

        if(t.isOpenDec()){
            throw new BaseException("已经开启申报");
        }

        if(!dao.updateOpenDec(id, true)){
            throw new BaseException("开启测评申报失败");
        }

        saveLog(id, "开启评测申报", username);
        publisher.publishEvent(new EvaOpenDecEvent(id));

        return get(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Evaluation closeDec(String id, String username){
        Evaluation t = get(id);

        if(!t.isOpenDec()){
            throw new BaseException("已经关闭申报");
        }

        if(!dao.updateOpenDec(id, false)){
            throw new BaseException("关闭申报失败");
        }

        saveLog(id, "关闭评测申报", username);
        publisher.publishEvent(new EvaCloseDecEvent(id));

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

    public List<EvaluationLog> queryLog(String evaId){
        return logDao.find(evaId);
    }

    static class ImportHistory {
        private final EvaItemDao itemDao;
        private final TaskCardDao taskCardDao;
        private final TaskItemDao taskItemDao;
        private final CompanyDao companyDao;
        private final GradeRateDao gradeRateDao;

        ImportHistory(EvaItemDao itemDao, TaskCardDao taskCardDao,
                      TaskItemDao taskItemDao, CompanyDao companyDao, GradeRateDao gradeRateDao) {

            this.itemDao = itemDao;
            this.taskCardDao = taskCardDao;
            this.taskItemDao = taskItemDao;
            this.companyDao = companyDao;
            this.gradeRateDao = gradeRateDao;
        }

        void importEvaluation(Evaluation evaluation, String importId, boolean isImportTask){
            Set<String> companyIdAll = getCompanyIdAll();
            Map<String, EvaItem> evaItemMap = importItem(evaluation, importId);
            Map<String, Integer> gradeRates= gradeRateDao.findAll().stream()
                    .collect(Collectors.toMap(GradeRate::getLevel, GradeRate::getRate));

            if(isImportTask){
                importTask(evaluation, importId, evaItemMap, companyIdAll, gradeRates);
            }
        }

        private Set<String> getCompanyIdAll(){
            return companyDao.find("", 0, Integer.MAX_VALUE).stream()
                    .map(Company::getId).collect(Collectors.toSet());
        }

        private Map<String, EvaItem> importItem(Evaluation evaluation, String importId){
            Map<String, EvaItem> itemMap = new LinkedHashMap<>();

            List<EvaItem> items = itemDao.findByEvaId(importId);

            items.forEach(e -> {
                String oId = e.getId();
                e.setId(IdGenerators.uuid());
                e.setEvaId(evaluation.getId());
                itemDao.insert(e);
                itemMap.put(oId, e);
            });

            return itemMap;
        }

        private void importTask(Evaluation evaluation, String importId, Map<String, EvaItem> evaItemMap,
                                Set<String> companyIdAll, Map<String, Integer> gradeRates){

            List<TaskCard> cards = taskCardDao.findByEvaId(importId);

            for(TaskCard card: cards){

                if(notHasCompany(card, companyIdAll)){
                    LOGGER.warn("Company not exist id={}", card.getCompanyId());
                   continue;
                }

                String cardId = card.getId();
                TaskCard newCard = newTaskCard(evaluation, card);
                List<TaskItem> taskItems = taskItemDao.findByCardId(cardId);
                for(TaskItem t: taskItems){
                    insertTaskItem(newCard, t, evaItemMap, gradeRates);
                }
            }
        }

        private boolean notHasCompany(TaskCard card, Set<String> companyIdAll){
            return !companyIdAll.contains(card.getCompanyId());
        }

        private TaskCard newTaskCard(Evaluation evaluation, TaskCard card){
            card.setId(IdGenerators.uuid());
            card.setEvaId(evaluation.getId());
            card.setEvaName(evaluation.getName());
            card.setOpen(false);
            card.setStatus(TaskCard.Status.WAIT);
            card.setGradeScore(0);

            taskCardDao.insert(card);

            return card;
        }

        private void insertTaskItem(TaskCard card, TaskItem taskItem,
                                    Map<String, EvaItem> itemMap, Map<String, Integer> gradeRates){

            EvaItem item = itemMap.get(taskItem.getEvaItemId());
            if(item == null){
                return ;
            }

            taskItem.setId(IdGenerators.uuid());
            taskItem.setCardId(card.getId());
            taskItem.setGradeScore(0);
            taskItem.setGrade(false);
            taskItem.setGradeRemark("");
            taskItem.setGradeLevel("");
            taskItem.setEvaItemId(item.getId());
            taskItem.setEvaNum(item.getNum());

            List<TaskItem.TaskItemResult> results = taskItem.getResults().stream()
                    .map(e -> newTaskItemResult(taskItem.getScore(), e, gradeRates))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            taskItem.setResults(results);

            taskItemDao.insert(taskItem);
        }

        private TaskItem.TaskItemResult newTaskItemResult(int totalScore, TaskItem.TaskItemResult result, Map<String, Integer> gradeRates){
            Integer rate = gradeRates.get(result.getLevel());
            if(rate == null){
                LOGGER.debug("Grade rate not exist level={}", result.getLevel());
                return null;
            }

            int score = totalScore * rate / 100;
            return new TaskItem.TaskItemResult(result.getLevel(), score);
        }
    }
}
