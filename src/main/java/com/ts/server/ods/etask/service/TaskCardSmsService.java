package com.ts.server.ods.etask.service;

import com.ts.server.ods.SmsProperties;
import com.ts.server.ods.base.domain.Member;
import com.ts.server.ods.base.service.MemberService;
import com.ts.server.ods.etask.domain.TaskCard;
import com.ts.server.ods.sms.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 任务卡短信服务
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Service
public class TaskCardSmsService {

    private final MemberService memberService;
    private final SmsService smsService;
    private final SmsProperties properties;

    @Autowired
    public TaskCardSmsService(MemberService memberService, SmsService smsService, SmsProperties properties) {
        this.memberService = memberService;
        this.smsService = smsService;
        this.properties = properties;
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
