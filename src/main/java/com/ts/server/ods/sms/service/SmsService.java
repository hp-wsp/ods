package com.ts.server.ods.sms.service;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.ts.server.ods.SmsProperties;
import com.ts.server.ods.base.domain.Member;
import com.ts.server.ods.base.service.MemberService;
import com.ts.server.ods.common.id.IdGenerators;
import com.ts.server.ods.sms.dao.SmsLogDao;
import com.ts.server.ods.sms.domain.SmsLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * 短信日志业务服务
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Service
@Transactional(readOnly = true)
public class SmsService {
    private final SmsLogDao dao;
    private final MemberService memberService;
    private final SmsSingleSender sender;
    private final SmsProperties properties;

    @Autowired
    public SmsService(SmsLogDao dao, MemberService memberService, SmsProperties properties) {
        this.dao = dao;
        this.memberService = memberService;
        this.sender = new SmsSingleSender(properties.getAppid(), properties.getAppKey());
        this.properties = properties;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void send(String phone, String content){
        SmsLog t = new SmsLog();
        t.setId(IdGenerators.uuid());
        t.setPhone(phone);
        t.setContent(content);
        getMember(phone).ifPresent(e -> {
            t.setCompanyName(e.getCompanyName());
            t.setName(e.getName());
        });

        try{
            SmsSingleSenderResult result = sender.send(0, "86", phone, content, "", "");
            t.setErrCode(result.result);
            t.setErrMsg(StringUtils.left(result.errMsg, 400));
        }catch (Exception e){
            t.setErrCode(-1000);
            t.setErrMsg(StringUtils.left(e.getMessage(), 400));
        }
        dao.insert(t);
    }

    private Optional<Member> getMember(String phone){
        return memberService.getUsername(phone);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void sendTemplate(String phone, String templateId, String[] params){
        sendTemplate(phone, templateId, params, e -> templateId + ":" + StringUtils.join(e, ";"));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void sendTemplate(String phone, String templateId, String[] params, Function<String[], String> funLogContent){
        SmsLog t = new SmsLog();
        t.setId(IdGenerators.uuid());
        t.setPhone(phone);
        t.setContent(funLogContent.apply(params));
        getMember(phone).ifPresent(e -> {
            t.setCompanyName(e.getCompanyName());
            t.setName(e.getName());
        });
        try{
            SmsSingleSenderResult result = sender.sendWithParam("86", phone,
                    Integer.valueOf(templateId), params,  properties.getSign(), "", "");
            t.setErrCode(result.result);
            t.setErrMsg(StringUtils.left(result.errMsg, 400));
        }catch (Exception e){
            t.setErrCode(-1000);
            t.setErrMsg(StringUtils.left(e.getMessage(), 400));
        }
        dao.insert(t);
    }

    public Long count(String phone, Boolean fail){
        return dao.count(phone, fail);
    }

    public List<SmsLog> query(String phone, Boolean fail, int offset, int limit){
        return dao.find(phone, fail, offset, limit);
    }
}
