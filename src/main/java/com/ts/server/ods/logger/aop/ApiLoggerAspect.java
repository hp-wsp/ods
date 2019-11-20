package com.ts.server.ods.logger.aop;

import com.ts.server.ods.logger.aop.annotation.*;
import com.ts.server.ods.logger.service.OptLogService;
import com.ts.server.ods.security.Credential;
import com.ts.server.ods.security.CredentialContextUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * API日志Aspect
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Aspect
@Component
public class ApiLoggerAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiLoggerAspect.class);

    private final OptLogService logService;
    private final ConcurrentMap<Class<? extends ApiLogDetailBuilder>, ApiLogDetailBuilder> builders;
    private final ConcurrentMap<Class<? extends ObtainUsername>, ObtainUsername> obtains;

    @Autowired
    public ApiLoggerAspect(OptLogService logService) {
        this.logService = logService;
        this.builders =  new ConcurrentHashMap<>();
        this.obtains = new ConcurrentHashMap<>();
    }

    @Pointcut("@annotation(com.ts.server.ods.logger.aop.annotation.EnableHttpLogger)")
    public void logging(){

    }

    @AfterReturning("@annotation(enableHttpLogger)")
    public void after(JoinPoint joinPoint, EnableApiLogger enableHttpLogger){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        String username = getUsername(joinPoint, attributes, enableHttpLogger.obtainUsername());

        ApiLogDetailBuilder builder = getInstance(builders, enableHttpLogger.buildDetail()).orElse(new NoneLogDetailBuilder());
        String detail = builder.build(joinPoint, attributes);

        logService.save(enableHttpLogger.name(), username, detail);
    }

    private <T> Optional<T> getInstance(ConcurrentMap<Class<? extends T>, T> map, Class<? extends T> clazz){
        T t = map.get(clazz);

        if(t != null){
            return Optional.of(t);
        }

        try{
            T newT = clazz.newInstance();
            t = map.putIfAbsent(clazz, newT);
            return Optional.of(t == null? newT: t);
        }catch (Exception e){
            LOGGER.error("Get api logger builder fail class={}, throw={}", clazz.getName(), e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 得到用户名
     *
     * @return 用户名
     */
    private String getUsername(JoinPoint joinPoint, ServletRequestAttributes attributes, Class<? extends  ObtainUsername> clazz){
        if(clazz != null){
            ObtainUsername obtainUsername = getInstance(obtains, clazz).orElse(new NoneObtainUsername());
            return obtainUsername.obtain(joinPoint, attributes);
        }

        Optional<Credential> optional = CredentialContextUtils.getCredential();
        return optional.map(Credential::getUsername).orElse("");
    }
}
