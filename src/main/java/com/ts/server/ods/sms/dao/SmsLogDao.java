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
        t.setCompanyName(r.getString("company_name"));
        t.setName(r.getString("name"));
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
        final String sql = "INSERT INTO l_sms (id, phone, content, company_name, name, err_code, err_msg, create_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, now())";
        jdbcTemplate.update(sql, t.getId(), t.getPhone(), t.getContent(), t.getCompanyName(), t.getName(), t.getErrCode(), t.getErrMsg());
    }

    public Long count(String phone, Boolean fail){
        String sql = "SELECT COUNT(id) FROM l_sms WHERE phone LIKE ?";
        if(fail != null && fail){
            sql = sql + " AND err_code != 0 ";
        }
        if(fail != null && !fail){
            sql = sql + " AND err_code = 0 ";
        }
        String phoneLike = DaoUtils.like(phone);
        return jdbcTemplate.queryForObject(sql, new Object[]{phoneLike}, Long.class);
    }

    public List<SmsLog> find(String phone, Boolean fail, int offset, int limit){
        String sql = "SELECT * FROM l_sms WHERE phone LIKE ? ";
        if(fail != null && fail){
            sql = sql + " AND err_code != 0 ";
        }
        if(fail != null && !fail){
            sql = sql + " AND err_code = 0 ";
        }
        sql = sql + " ORDER BY create_time DESC LIMIT ? OFFSET ?";
        String phoneLike = DaoUtils.like(phone);
        return jdbcTemplate.query(sql, new Object[]{phoneLike, limit,offset}, mapper);
    }
}
