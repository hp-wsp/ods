package com.ts.server.ods.taskcard.listener;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.base.domain.Member;
import com.ts.server.ods.base.event.MemberEvent;
import com.ts.server.ods.base.service.MemberService;
import com.ts.server.ods.taskcard.service.TaskCardService;
import com.ts.server.ods.evaluation.service.EvaluationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 申报人员事件侦听
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Component
public class MemberEventListener {

    private final EvaluationService evaService;
    private final TaskCardService cardService;
    private final MemberService memberService;

    @Autowired
    public MemberEventListener(EvaluationService evaService, TaskCardService cardService,
                               MemberService memberService) {

        this.evaService = evaService;
        this.cardService = cardService;
        this.memberService = memberService;
    }

    @EventListener
    public void listener(MemberEvent event){
        if(isUpdate(event.getEvent())){
            updateDecMember(event.getId());
        }

        if (isDelete(event.getEvent())) {
            validateDelete(event.getId());
        }
    }

    private boolean isUpdate(String event){
        return StringUtils.equals(event, "update");
    }

    private void updateDecMember(String id){
        Member member = memberService.get(id);
        evaService.queryActive().forEach(e -> cardService.updateDecMember(e.getId(), member));
    }

    private boolean isDelete(String event){
        return StringUtils.equals(event, "delete");
    }

    private void validateDelete(String id){
        boolean has = evaService.queryActive().stream().anyMatch(e -> cardService.hasDecMemberByEvaId(e.getId(), id));
        if(has){
            throw new BaseException("已经分配测评任务，不能删除");
        }
    }
}
