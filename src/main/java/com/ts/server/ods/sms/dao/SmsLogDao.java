package com.ts.server.ods.sms.dao;

import com.ts.server.ods.common.utils.DaoUtils;
import com.ts.server.ods.sms.domain.SmsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

/**
 * 短信日志数据操作
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Repository
public class SmsLogDao {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<SmsLog> mapper = (r, i) -> {
        SmsLog t = new SmsLog();

        t.setId(r.getString("id"));
        t.setPhone(r.getString("phone"));
        t.setContent(r.getString("content"));
        t.setErrCode(r.getInt("err_code"));
        t.setErrMsg(r.getString("err_msg"));
        t.setCreateTime(r.getTimestamp("create_time"));

        return t;
    };

    @Autowired
    public SmsLogDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void insert(SmsLog t){
        final String sql = "INSERT INTO l_sms (id, phone, content, err_code, err_msg, create_time) " +
                "VALUES (?, ?, ?, ?, ?, now())";
        jdbcTemplate.update(sql, t.getId(), t.getPhone(), t.getContent(), t.getErrCode(), t.getErrMsg());
    }

    public Long count(String phone){
        final String sql = "SELECT COUNT(id) FROM l_sms WHERE phone LIKE ?";
        String phoneLike = DaoUtils.like(phone);
        return jdbcTemplate.queryForObject(sql, new Object[]{phoneLike}, Long.class);
    }

    public List<SmsLog> find(String phone, int offset, int limit){
        final String sql = "SELECT * FROM l_sms WHERE phone LIKE ? ORDER BY create_time DESC LIMIT ? OFFSET ?";
        String phoneLike = DaoUtils.like(phone);
        return jdbcTemplate.query(sql, new Object[]{phoneLike, limit,offset}, mapper);
    }
}
