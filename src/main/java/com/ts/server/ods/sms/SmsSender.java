package com.ts.server.ods.sms;

import com.ts.server.ods.SmsProperties;
import com.ts.server.ods.base.domain.Member;
import com.ts.server.ods.base.service.MemberService;
import com.ts.server.ods.evaluation.domain.Evaluation;
import com.ts.server.ods.taskcard.domain.TaskCard;
import com.ts.server.ods.sms.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * 短信服务
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Component
public class SmsSender {

    private final MemberService memberService;
    private final SmsService smsService;
    private final SmsProperties properties;

    @Autowired
    public SmsSender(MemberService memberService, SmsService smsService, SmsProperties properties) {
        this.memberService = memberService;
        this.smsService = smsService;
        this.properties = properties;
    }

    /**
     * 发送申报短信
     *
     * @param member {@link Member}
     * @param evaluation {@link Evaluation}
     */
    public void launch(Member member, Evaluation evaluation){
        String[] params = buildLaunchParams(member, evaluation);
        smsService.sendTemplate(member.getPhone(), properties.getApplyTmp(), params,
                e -> String.format("%s申报已经开始; 登录平台 用户名: %s; 密码:%s; 申报时间:%s - %s;",
                        params[0], params[1], "******", params[3], params[4]));
    }

    private String[] buildLaunchParams(Member member, Evaluation evaluation){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return new String[]{ evaluation.getName(), member.getUsername(), member.getPassword(),
                dateFormat.format(evaluation.getFromTime()), dateFormat.format(evaluation.getToTime())};
    }

    /**
     * 发送催报短信
     *
     * @param card {@link TaskCard}
     * @param content 催促内容
     */
    public void urge(TaskCard card, String content){
        Member member = memberService.get(card.getDecId());
        smsService.sendTemplate(member.getPhone(), properties.getUrgeTmp(), new String[]{card.getEvaName(), content},
                e -> String.format("%s申报即将结束请尽快完成申报；提醒注意事项%s", e[0], e[1]));
    }

    /**
     * 发送退回通知短信
     *
     * @param card {@link TaskCard}
     */
    public void back(TaskCard card){
        Member member = memberService.get(card.getDecId());
        smsService.sendTemplate(member.getPhone(), properties.getBackTmp(), new String[]{card.getEvaName()},
                e ->  String.format("%s材料存在问题，请根据问题说明整改问题材料，重新上报", e[0]));
    }

    /**
     * 发送退回通知短信
     *
     * @param card {@link TaskCard}
     */
    public void cancelBack(TaskCard card){
        Member member = memberService.get(card.getDecId());
        smsService.sendTemplate(member.getPhone(), properties.getCancelBackTmp(), new String[]{card.getEvaName()},
                e ->  String.format("%s材料存在问题，请根据问题说明整改问题材料，重新上报", e[0]));
    }
}
