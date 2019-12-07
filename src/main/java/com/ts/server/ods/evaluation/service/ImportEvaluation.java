package com.ts.server.ods.evaluation.service;

import com.ts.server.ods.base.dao.CompanyDao;
import com.ts.server.ods.base.dao.GradeRateDao;
import com.ts.server.ods.base.domain.Company;
import com.ts.server.ods.base.domain.GradeRate;
import com.ts.server.ods.common.id.IdGenerators;
import com.ts.server.ods.etask.dao.TaskCardDao;
import com.ts.server.ods.etask.dao.TaskItemDao;
import com.ts.server.ods.etask.domain.TaskCard;
import com.ts.server.ods.etask.domain.TaskItem;
import com.ts.server.ods.evaluation.dao.EvaItemDao;
import com.ts.server.ods.evaluation.domain.EvaItem;
import com.ts.server.ods.evaluation.domain.Evaluation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 导入历史测评
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
class ImportEvaluation {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImportEvaluation.class);

    private final EvaItemDao itemDao;
    private final TaskCardDao taskCardDao;
    private final TaskItemDao taskItemDao;
    private final CompanyDao companyDao;
    private final GradeRateDao gradeRateDao;

    ImportEvaluation(EvaItemDao itemDao, TaskCardDao taskCardDao,
                     TaskItemDao taskItemDao, CompanyDao companyDao,
                     GradeRateDao gradeRateDao) {

        this.itemDao = itemDao;
        this.taskCardDao = taskCardDao;
        this.taskItemDao = taskItemDao;
        this.companyDao = companyDao;
        this.gradeRateDao = gradeRateDao;
    }

    void importEvaluation(Evaluation evaluation, String importId){
        Set<String> companyIdAll = getCompanyIdAll();
        Map<String, EvaItem> evaItemMap = importItem(evaluation, importId);
        Map<String, Integer> gradeRates= gradeRateDao.findAll().stream()
                .collect(Collectors.toMap(GradeRate::getLevel, GradeRate::getRate));

        importTask(evaluation, importId, evaItemMap, companyIdAll, gradeRates);
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
