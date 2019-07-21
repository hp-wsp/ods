package com.ts.server.ods.security.token.simple.repository;

import com.ts.server.ods.security.Credential;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 实现内存方式token存储
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Repository
public class MemeryTokenRepository implements TokenRepository {
    private static final ConcurrentMap<String, Credential> POOL = new ConcurrentHashMap<>();

    @Override
    public boolean save(String token, Credential t) {
        POOL.put(token, t);
        return true;
    }

    @Override
    public Optional<Credential> get(String token) {
        return Optional.ofNullable(POOL.get(token));
    }

    @Override
    public boolean remove(String token) {
        POOL.remove(token);
        return true;
    }

    @Override
    public void clear() {
        List<String> tokens = POOL.entrySet().stream()
                .filter(e -> e.getValue().isExpired())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        tokens.forEach(POOL::remove);
    }
}
