package com.ts.server.ods.logger.aop.annotation;

import com.ts.server.ods.security.Credential;
import com.ts.server.ods.security.CredentialContextUtils;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * 不获取用户名
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class CredentialObtainUsername implements ObtainUsername {

    @Override
    public String obtain(JoinPoint joinPoint, ServletRequestAttributes attributes) {
        Optional<Credential> optional = CredentialContextUtils.getCredential();
        return optional.map(Credential::getUsername).orElse("");
    }
}
