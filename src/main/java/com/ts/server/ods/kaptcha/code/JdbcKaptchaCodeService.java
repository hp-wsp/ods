package com.ts.server.ods.kaptcha.code;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Date;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class JdbcKaptchaCodeService implements KaptchaCodeService {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcKaptchaCodeService(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void save(String codeKey, String code) {
        final String sql = "INSERT INTO l_kaptcha (code_key, code_value, create_time) VALUES (?, ?, now())";
        jdbcTemplate.update(sql, codeKey, code);
    }

    @Override
    public Optional<String> get(String codeKey) {
        final String sql = "SELECT code_value FROM l_kaptcha WHERE code_key = ?";
        try{
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new Object[]{codeKey}, String.class));
        }catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void clearExpired(Date expiredTime) {
        final String sql = "DELETE FROM l_kaptcha WHERE create_time < ?";
        jdbcTemplate.update(sql, expiredTime);
    }
}
