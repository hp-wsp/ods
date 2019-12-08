package com.ts.server.ods.taskcard.service;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.base.domain.Company;
import com.ts.server.ods.base.domain.Manager;
import com.ts.server.ods.base.domain.Member;
import com.ts.server.ods.base.service.CompanyService;
import com.ts.server.ods.base.service.ManagerService;
import com.ts.server.ods.base.service.MemberService;
import com.ts.server.ods.common.id.IdGenerators;
import com.ts.server.ods.taskcard.dao.DeclarationDao;
import com.ts.server.ods.taskcard.dao.TaskCardDao;
import com.ts.server.ods.taskcard.dao.TaskCardItemDao;
import com.ts.server.ods.taskcard.domain.TaskCard;
import com.ts.server.ods.taskcard.domain.TaskCardItem;
import com.ts.server.ods.evaluation.domain.Evaluation;
import com.ts.server.ods.evaluation.service.EvaluationService;
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

/**
 * 测评任务卡业务服务
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Service
@Transactional(readOnly = true)
public class TaskCardService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskCardService.class);

    private final TaskCardDao dao;
    private final TaskCardItemDao itemDao;
    private final DeclarationDao decDao;
    private final EvaluationService evaluationService;
    private final ManagerService managerService;
    private final MemberService memberService;
    private final CompanyService companyService;

    @Autowired
    public TaskCardService(TaskCardDao dao, TaskCardItemDao itemDao, DeclarationDao decDao, EvaluationService evaluationService,
                           ManagerService managerService, MemberService memberService, CompanyService companyService) {

        this.dao = dao;
        this.itemDao = itemDao;
        this.decDao = decDao;
        this.evaluationService = evaluationService;
        this.managerService = managerService;
        this.memberService = memberService;
        this.companyService = companyService;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TaskCard save(TaskCard t){

        if(dao.hasCompanyByEvaId(t.getEvaId(), t.getCompanyId())){
            throw new BaseException("单位测评已经存在");
        }

        Manager assManager = managerService.get(t.getAssId());
        t.setAssId(assManager.getId());
        t.setAssUsername(assManager.getUsername());
        t.setAssName(assManager.getName());

        Company company = companyService.get(t.getCompanyId());
        t.setCompanyName(company.getName());
        t.setCompanyGroup(company.getGroup());
        t.setCompanyGroupNum(company.getGroupNum());

        Member member = getDecMember(t);
        t.setDecId(member.getId());
        t.setDecUsername(member.getUsername());
        t.setDecName(member.getName());

        Evaluation evaluation = evaluationService.get(t.getEvaId());
        t.setEvaName(evaluation.getName());
        t.setOpenGrade(evaluation.getStatus() == Evaluation.Status.OPEN);
        t.setOpen(evaluation.isOpenDec());
        t.setStatus(TaskCard.Status.WAIT);

        t.setId(IdGenerators.uuid());
        dao.insert(t);

        return dao.findOne(t.getId());
    }

    private Member getDecMember(TaskCard t){
        return StringUtils.isBlank(t.getDecId())?
                memberService.getManager(t.getCompanyId()): memberService.get(t.getDecId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TaskCard update(TaskCard t){

        Manager assManager = managerService.get(t.getAssId());
        t.setAssId(assManager.getId());
        t.setAssUsername(assManager.getUsername());
        t.setAssName(assManager.getName());

        Company company = companyService.get(t.getCompanyId());
        t.setCompanyName(company.getName());
        t.setCompanyGroup(company.getGroup());
        t.setCompanyGroupNum(company.getGroupNum());

        Member member = getDecMember(t);
        t.setDecId(member.getId());
        t.setDecUsername(member.getUsername());
        t.setDecName(member.getName());

        if(!dao.update(t)){
            throw new BaseException("修改任务卡失败");
        }

        return dao.findOne(t.getId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateDecMember(String evaId, Member member){
        dao.updateDecMember(evaId, member.getId(), member.getUsername(), member.getName());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateEvaluation(Evaluation t){
        boolean isGrade = t.getStatus() == Evaluation.Status.OPEN;
        dao.updateEvaluation(t.getId(), t.getName(), t.isOpenDec(), isGrade);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCompany(String evaId, Company company){
        dao.updateCompany(evaId, company.getId(), company.getName(), company.getGroup(), company.getGroupNum());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateAssManager(String evaId, Manager manager){
        dao.updateAssManager(evaId, manager.getId(), manager.getUsername(), manager.getName());
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

        boolean notHas = itemDao.findByCardId(id).stream().anyMatch(e -> !decDao.hasByItemId(e.getId()));
        if(notHas){
            throw new BaseException("还有未完评审成");
        }

        if(!dao.updateStatus(id, TaskCard.Status.SUBMIT)){
            throw new BaseException("提交失败");
        }

        return get(id);
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

        return get(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TaskCard cancelBack(String id, String username){
        TaskCard card = get(id);

        if(card.getStatus() != TaskCard.Status.BACK){
            throw new BaseException( "还未退回");
        }

        if(!dao.updateStatus(id, TaskCard.Status.SUBMIT)){
            throw new BaseException("撤销退回失败");
        }

        return get(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public TaskCard finish(String id){
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

        boolean notGrade =  itemDao.findByCardId(id).stream().anyMatch(e -> StringUtils.isBlank(e.getGradeLevel()));
        if(notGrade){
            throw new BaseException("有测评项目未打分");
        }

        if(!dao.updateStatus(id, TaskCard.Status.GRADE)){
            throw new BaseException("测评完成失败");
        }

        return get(id);
    }

    @Transactional(propagation =  Propagation.REQUIRED)
    public boolean delete(String id){
        boolean hasDec = itemDao.findByCardId(id).stream().anyMatch(e -> decDao.hasByItemId(e.getId()));
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
    public void updateItemStats(String id){
        Integer count = itemDao.countByCardId(id);
        int score = itemDao.findByCardId(id).stream().mapToInt(TaskCardItem::getScore).sum();
        dao.updateItemStats(id, count, score);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean updateDecCount(String id){
        Integer count = itemDao.countDecByCardId(id);
        return dao.updateDecCount(id, count);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateGradeScore(String id){
        List<TaskCardItem> items = itemDao.findByCardId(id);

        int total = items.stream()
                .filter(e -> e.getGradeScore() > 0)
                .mapToInt(TaskCardItem::getGradeScore)
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

    public boolean hasByEvaId(String evaId){
        return dao.hasByEvaId(evaId);
    }

    public boolean hasCompanyByEvaId(String evaId, String companyId){
        return dao.hasCompanyByEvaId(evaId, companyId);
    }

    public boolean hasAssManagerByEvaId(String evaId, String manId){
        return dao.hasAssManagerByEvaId(evaId, manId);
    }

    public boolean hasDecMemberByEvaId(String evaId, String memId){
        return dao.hasDecMemberByEvaId(evaId, memId);
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
