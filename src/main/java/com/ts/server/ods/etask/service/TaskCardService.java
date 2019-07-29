package com.ts.server.ods.etask.service;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.base.domain.Company;
import com.ts.server.ods.base.domain.Manager;
import com.ts.server.ods.base.domain.Member;
import com.ts.server.ods.base.service.CompanyService;
import com.ts.server.ods.base.service.ManagerService;
import com.ts.server.ods.base.service.MemberService;
import com.ts.server.ods.common.id.IdGenerators;
import com.ts.server.ods.etask.dao.DeclarationDao;
import com.ts.server.ods.etask.dao.TaskCardDao;
import com.ts.server.ods.evaluation.dao.EvaluationLogDao;
import com.ts.server.ods.etask.dao.TaskItemDao;
import com.ts.server.ods.etask.domain.TaskCard;
import com.ts.server.ods.evaluation.domain.EvaluationLog;
import com.ts.server.ods.etask.domain.TaskItem;
import com.ts.server.ods.evaluation.domain.Evaluation;
import com.ts.server.ods.evaluation.service.EvaluationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 测评任务卡业务服务
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Service
@Transactional(readOnly = true)
public class TaskCardService {
    private final TaskCardDao dao;
    private final TaskItemDao itemDao;
    private final EvaluationLogDao logDao;
    private final DeclarationDao decDao;
    private final EvaluationService evaluationService;
    private final ManagerService managerService;
    private final MemberService memberService;
    private final CompanyService companyService;

    @Autowired
    public TaskCardService(TaskCardDao dao, TaskItemDao itemDao, DeclarationDao decDao,
                           EvaluationLogDao taskCardLogDao, EvaluationService evaluationService,
                           ManagerService managerService, MemberService memberService, CompanyService companyService) {

        this.dao = dao;
        this.itemDao = itemDao;
        this.logDao = taskCardLogDao;
        this.decDao = decDao;
        this.evaluationService = evaluationService;
        this.managerService = managerService;
        this.memberService = memberService;
        this.companyService = companyService;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TaskCard save(TaskCard t){

        if(dao.hasCompany(t.getEvaId(), t.getCompanyId())){
            throw new BaseException("单位测评已经存在");
        }

        Manager assManager = managerService.get(t.getAssId());
        t.setAssId(assManager.getId());
        t.setAssUsername(assManager.getUsername());
        t.setAssName(assManager.getName());

        Company company = companyService.get(t.getCompanyId());
        t.setCompanyName(company.getName());
        t.setCompanyGroup(company.getGroup());

        Optional<Member> memberOptional = memberService.getUsername(company.getPhone());
        if(!memberOptional.isPresent()){
            throw new BaseException("申报人员不存在");
        }

        Member member = memberOptional.get();
        if(!StringUtils.equals(t.getCompanyId(), member.getCompanyId())){
            throw new BaseException("申报人员单位不正确");
        }
        t.setDecId(member.getId());
        t.setDecUsername(member.getUsername());
        t.setDecName(member.getName());

        Evaluation evaluation = evaluationService.get(t.getEvaId());
        t.setEvaName(evaluation.getName());
        t.setOpen(evaluation.getStatus() == Evaluation.Status.OPEN);
        t.setStatus(TaskCard.Status.WAIT);

        t.setId(IdGenerators.uuid());
        dao.insert(t);

        return dao.findOne(t.getId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TaskCard update(TaskCard t){

        TaskCard o = get(t.getId());
        t.setDecId(o.getDecId());
        t.setDecUsername(o.getDecUsername());
        t.setDecName(o.getDecName());

        Manager assManager = managerService.get(t.getAssId());
        t.setAssId(assManager.getId());
        t.setAssUsername(assManager.getUsername());
        t.setAssName(o.getAssName());

        Company company = companyService.get(t.getCompanyId());
        t.setCompanyName(company.getName());
        t.setCompanyGroup(company.getGroup());

        if(!dao.update(t)){
            throw new BaseException("修改任务卡失败");
        }

        return dao.findOne(t.getId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateDec(String evaId, String companyId, Member member){
        dao.updateDec(evaId,companyId, member.getId(), member.getUsername(), member.getName());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void open(String evaId){
        dao.updateOpen(evaId, true);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void close(String evaId){
        dao.updateOpen(evaId, false);
    }

    public TaskCard get(String id){
        try{
            return dao.findOne(id);
        }catch (DataAccessException e){
            throw new BaseException("任务卡不存在");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TaskCard submit(String id, Member member){
        TaskCard card = get(id);
        if(!StringUtils.equals(card.getDecId(), member.getId())){
            throw new BaseException("权限不够不能提交");
        }

        if(card.getStatus() == TaskCard.Status.SUBMIT){
            throw new BaseException( "已经提交");
        }

        if(card.getStatus() == TaskCard.Status.GRADE){
            throw new BaseException( "已经打分");
        }

        List<TaskItem> items = itemDao.findByCardId(id);
        for(TaskItem item: items){
            if(!decDao.hasByItemId(item.getId())){
                throw new BaseException("还有未完评审成");
            }
        }

        if(!dao.updateStatus(id, TaskCard.Status.SUBMIT)){
            throw new BaseException("提交失败");
        }

        saveLog(card.getEvaId(), String.format("%s提交申报", card.getCompanyName()), member.getUsername());

        return get(id);
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

    @Transactional(propagation = Propagation.REQUIRED)
    public TaskCard back(String id, String username){
        TaskCard card = get(id);

        if(card.getStatus() == TaskCard.Status.WAIT){
            throw new BaseException( "还未提交");
        }

        if(card.getStatus() == TaskCard.Status.BACK){
            throw new BaseException( "已经退回");
        }

        if(!dao.updateStatus(id, TaskCard.Status.BACK)){
            throw new BaseException("退回失败");
        }

        saveLog(card.getEvaId(), String.format("%s申报被退回", card.getCompanyName()), username);

        return get(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TaskCard finish(String id, String username){
        TaskCard card = get(id);

        if(card.getStatus() == TaskCard.Status.WAIT){
            throw new BaseException( "还未提交");
        }

        if(card.getStatus() == TaskCard.Status.BACK){
            throw new BaseException( "已经退回");
        }

        if(card.getStatus() == TaskCard.Status.GRADE){
            throw new BaseException("已经完成");
        }

        List<TaskItem> items = itemDao.findByCardId(id);
        boolean notGrade = items.stream().anyMatch(e -> StringUtils.isBlank(e.getGradeLevel()));
        if(notGrade){
            throw new BaseException("有测评项目未打分");
        }

        if(!dao.updateStatus(id, TaskCard.Status.GRADE)){
            throw new BaseException("完成失败");
        }

        int score = items.stream().mapToInt(TaskItem::getGradeScore).sum();
        if(!dao.updateGradeScore(id, score)){
            throw new BaseException("汇总分数失败");
        }

        TaskCard newCard = get(id);
        saveLog(card.getEvaId(), String.format("%s申报评分%d", card.getCompanyName(), card.getGradeScore()),username);

        return newCard;
    }

    @Transactional(propagation =  Propagation.REQUIRED)
    public boolean delete(String id){
        List<TaskItem> items = itemDao.findByCardId(id);
        boolean hasDec = items.stream().anyMatch(e -> decDao.hasByItemId(e.getId()));
        if(hasDec){
            throw new BaseException("测评任务已经申报不能删除");
        }

        boolean ok = dao.delete(id);
        if(ok){
            itemDao.deleteByCardId(id);
        }

        return ok;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateScore(String id){
        List<TaskItem> items = itemDao.findByCardId(id);
        int total = items.isEmpty()? 0: items.stream()
                .mapToInt(TaskItem::getScore).sum();

        dao.updateScore(id, total);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean updateItemCount(String id){
        Integer count = itemDao.countByCardId(id);
        return dao.updateItemCount(id, count);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean updateDecCount(String id){
        Integer count = itemDao.countDecByCardId(id);
        return dao.updateDecCount(id, count);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateGradeScore(String id){
        List<TaskItem> items = itemDao.findByCardId(id);

        int total = items.stream()
                .filter(e -> e.getGradeScore() > 0)
                .mapToInt(TaskItem::getGradeScore)
                .sum();

        dao.updateGradeScore(id, total);
    }

    public Long count(String evaId, String companyName, String assUsername, String decUsername){
        return dao.count(evaId, companyName, assUsername, decUsername);
    }

    public List<TaskCard> query(String evaId, String companyName, String assUsername, String decUsername, int offset, int limit){
        return dao.find(evaId, companyName, assUsername, decUsername, offset, limit);
    }

    public List<TaskCard> queryOpenByDecId(String decId){
        return dao.findOpenByDecId(decId);
    }

    public Long countOpenByAssId(String assId, String company){
        return dao.countOpenByAssId(assId, company);
    }

    public List<TaskCard> queryOpenByAssId(String assId, String company, int offset, int limit){
        return dao.findOpenByAssId(assId, company, offset, limit);
    }

    public List<TaskCard> queryByEvaId(String evaId){
        return dao.findByEvaId(evaId);
    }

    public Long countGrade(String evaId, String companyName){
        return dao.countGrade(evaId, companyName);
    }

    public List<TaskCard> queryGrade(String evaId, String companyName, int offset, int limit){
        return dao.findGrade(evaId, companyName, offset, limit);
    }

    public Map<String, Integer> queryGroupStatus(String evaId){
        return dao.findGroupStatus(evaId);
    }

}
