package com.ts.server.ods.evaluation.service;

import com.ts.server.ods.base.service.MemberService;
import com.ts.server.ods.sms.SmsSender;
import com.ts.server.ods.taskcard.service.TaskCardService;
import com.ts.server.ods.evaluation.domain.Evaluation;
import com.ts.server.ods.evaluation.runner.LaunchSmsRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 评测定时任务处理
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Component
public class EvaluationScheduler {
    private final Timer timer = new Timer("SMS_SEND");
    private final EvaluationService service;
    private final TaskCardService taskCardService;
    private final MemberService memberService;
    private final SmsSender smsSender;

    @Autowired
    public EvaluationScheduler(EvaluationService service, TaskCardService taskCardService,
                               MemberService memberService, SmsSender smsSender) {

        this.service = service;
        this.taskCardService = taskCardService;
        this.memberService = memberService;
        this.smsSender = smsSender;
    }

    @Scheduled(fixedDelay = 60000L, initialDelay = 60000L)
    public void openDecSchedule(){
        List<Evaluation> lst = service.query("", Evaluation.Status.OPEN, 0, 50);
        lst.stream().filter(this::isOpenDec).forEach(this::openDec);
    }

    /**
     * 判断是否开启申报
     *
     * @param t {@link Evaluation}
     * @return true:开启申报
     */
    private boolean isOpenDec(Evaluation t){
        long now = System.currentTimeMillis();
        return t.isAuto() && !t.isOpenDec() && (t.getFromTime().getTime()<= now && t.getToTime().getTime() >= now);
    }

    /**
     * 打开评测申报
     *
     * @param t {@link Evaluation}
     */
    private void openDec(Evaluation t){
        service.openDec(t.getId());

        if(t.isSms()){
            return ;
        }

        int hour = LocalTime.now().getHour();
        LaunchSmsRunner runner = new LaunchSmsRunner(service, taskCardService, memberService, t.getId(), smsSender);
        if(hour > 8 && hour <22){
            runner.run();
            return ;
        }

        //延迟到工作时间发送
        int delayHours = hour < 9? 9 - hour: (23 - hour) + 9;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runner.run();
            }
        }, delayHours * 3600 * 1000L);
    }

    @Scheduled(fixedDelay = 60000L, initialDelay = 60000L)
    public void closeDecSchedule(){
        List<Evaluation> lst = service.query("", Evaluation.Status.OPEN, 0, 50);
        long now = System.currentTimeMillis();
        lst.stream().filter(e -> isClose(now, e))
                .forEach(e -> service.updateStatus(e.getId(), Evaluation.Status.CLOSE));
    }

    private boolean isClose(long now, Evaluation e){
        return e.isAuto() && e.isOpenDec() && e.getToTime().getTime() <= now;
    }
}