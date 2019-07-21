package com.ts.server.ods.security.token.simple;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.common.utils.SecurityUtils;
import com.ts.server.ods.security.Credential;
import com.ts.server.ods.security.token.TokenService;
import com.ts.server.ods.security.token.simple.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 访问Token服务
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Service
public class SimpleTokenService implements TokenService {
    
    private final TokenRepository repos;

    @Autowired
    public SimpleTokenService(TokenRepository repos){
    	this.repos = repos;
    }

    @Override
    public String generate(Credential credential) {
        String token = SecurityUtils.randomStr(64);
        if(!repos.save(token, credential)){
            throw new BaseException(2001,"创建认证失败");
        }
        return token;
    }

    @Override
    public boolean update(String token, Credential t) {
        return repos.save(token, t);
    }

    @Override
    public Credential validateAndGet(String token) {
        Optional<Credential> t = repos.get(token);
        return t.orElseThrow(() -> new BaseException(110, "认证不存在"));
    }

    @Override
    public void destroy(String token) {
        repos.remove(token);
    }

    @Scheduled(fixedDelay = 5 * 60 * 1000L)
    public void clearExpired(){
        repos.clear();
    }
}
