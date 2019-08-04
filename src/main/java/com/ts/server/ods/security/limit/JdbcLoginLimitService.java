package com.ts.server.ods.security.limit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 通过数据库实现{@link LoginLimitService}
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Service
@Transactional(readOnly = true)
public class JdbcLoginLimitService implements LoginLimitService {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcLoginLimitService.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcLoginLimitService(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int getFail(String username) {
        String day = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        return getFailCount(username, day);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int incFail(String username) {
        String day = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        int count = getFailCount(username, day);
        if(count == 0){
            insertFail(username, day);
            return 1;
        }
        incFailCount(username, day);
        return count + 1;
    }

    private int getFailCount(String username, String day){
        final String sql = "SELECT SUM(l_count) FROM l_login_limit WHERE username = ? AND l_day = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{username, day}, Integer.class);
        return count == null? 0: count;
    }

    private void incFailCount(String username , String day){
        final String sql = "UPDATE l_login_limit SET l_count = l_count + 1 WHERE username = ? AND l_day = ?";
        jdbcTemplate.update(sql, username, day);
    }

    private void insertFail(String username, String day){
        final String sql = "INSERT INTO l_login_limit (username, l_day, l_count) VALUES (?, ?, 1)";
        try{
            jdbcTemplate.update(sql, username, day);
        }catch (DataAccessException e){
            LOGGER.error("Insert login limit fail throw={}", e.getMessage());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void resetFail(String username) {
        final String sql = "UPDATE l_login_limit SET l_count = ? WHERE username=? AND l_day = ?";
        String day = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        jdbcTemplate.update(sql, 0, username, day);
    }
}
