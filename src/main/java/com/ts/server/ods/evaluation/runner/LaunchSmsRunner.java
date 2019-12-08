package com.ts.server.ods.evaluation.runner;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.SmsProperties;
import com.ts.server.ods.base.domain.Member;
import com.ts.server.ods.base.service.MemberService;
import com.ts.server.ods.sms.SmsSender;
import com.ts.server.ods.taskcard.domain.TaskCard;
import com.ts.server.ods.taskcard.service.TaskCardService;
import com.ts.server.ods.evaluation.domain.Evaluation;
import com.ts.server.ods.evaluation.service.EvaluationService;
import com.ts.server.ods.exec.ProgressRunnable;
import com.ts.server.ods.sms.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 发送开始通知短信
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class LaunchSmsRunner implements ProgressRunnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(LaunchSmsRunner.class);

    private final EvaluationService evaService;
    private final TaskCardService taskCardService;
    private final MemberService memberService;
    private final String evaId;
    private final SmsSender smsSender;

    public LaunchSmsRunner(EvaluationService evaService, TaskCardService taskCardService, MemberService memberService,
                           String evaId, SmsSender smsSender) {

        this.evaService = evaService;
        this.taskCardService = taskCardService;
        this.memberService = memberService;
        this.evaId = evaId;
        this.smsSender = smsSender;
    }

    private volatile int progress;

    @Override
    public int progress() {
        return progress;
    }

    @Override
    public void run() {
        Evaluation evaluation = evaService.get(evaId);
        if(evaluation.getStatus() != Evaluation.Status.OPEN || !evaluation.isOpenDec()){
            LOGGER.warn("Evaluation is not open skipp send sms evaId={}", evaId);
            throw new BaseException("测评申报还未开启");
        }

        if (!evaluation.isSms()){
            evaService.sendSms(evaId);
        }

        List<TaskCard> cards = taskCardService.queryByEvaId(evaId);
        int count = cards.size();
        LOGGER.debug("Send evaluation start sms evaId={}, count={}", evaId, count);

        int sendCount = 0;
        for(TaskCard card: cards){

            try{
                List<Member> members = getMembers(card.getDecId());
                for(Member member: members){
                    sendSms(member, evaluation);
                }
            }catch (Exception e){
                LOGGER.debug("Send launch sms fail cardId={}, companyName={}, sendCount={}, throw={}",
                        card.getId(), card.getCompanyName(), sendCount, e.getMessage());
            }

            sendCount = sendCount + 1;
            progress = (sendCount * 100)/ count;
        }

        progress = 100;
    }

    private List<Member> getMembers(String memberId){
        Member member = memberService.get(memberId);
        if(member.isManager()){
            return Collections.singletonList(member);
        }

        List<Member> members = memberService.queryByCompanyId(member.getCompanyId()).stream()
                .filter(Member::isManager).collect(Collectors.toList());
        Collections.addAll(members, member);
        return members;
    }

    private void sendSms(Member member, Evaluation evaluation){
        smsSender.launch(member, evaluation);
    }
}
