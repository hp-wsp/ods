package com.ts.server.ods.sms.service;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.ts.server.ods.SmsProperties;
import com.ts.server.ods.common.id.IdGenerators;
import com.ts.server.ods.sms.dao.SmsLogDao;
import com.ts.server.ods.sms.domain.SmsLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    private final SmsSingleSender sender;
    private final SmsProperties properties;

    @Autowired
    public SmsService(SmsLogDao dao, SmsProperties properties) {
        this.dao = dao;
        this.sender = new SmsSingleSender(properties.getAppid(), properties.getAppKey());
        this.properties = properties;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void send(String phone, String content){
        SmsLog t = new SmsLog();
        t.setId(IdGenerators.uuid());
        t.setPhone(phone);
        t.setContent(content);
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

    public Long count(String phone){
        return dao.count(phone);
    }

    public List<SmsLog> query(String phone, int offset, int limit){
        return dao.find(phone, offset, limit);
    }
}
