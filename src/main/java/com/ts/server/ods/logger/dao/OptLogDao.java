package com.ts.server.ods.logger.dao;

import com.ts.server.ods.common.utils.DaoUtils;
import com.ts.server.ods.logger.domain.OptLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

/**
 * 操作日志数据操作
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Repository
public class OptLogDao {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<OptLog> mapper = (r, i) -> {
        OptLog  t = new OptLog();

        t.setId(r.getLong("id"));
        t.setDetail(r.getString("detail"));
        t.setParams(r.getString("params"));
        t.setUsername(r.getString("username"));
        t.setCreateTime(r.getTimestamp("create_time"));

        return t;
    };

    @Autowired
    public OptLogDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void insert(OptLog t){
        final String sql = "INSERT INTO l_operator (detail, params, username, create_time) VALUES (?, ?, ?, now())";
        jdbcTemplate.update(sql, t.getDetail(), t.getParams(), t.getUsername());
    }

    public Long count(String detail, String params, String username, Date fromDate, Date toDate){
        String detailLike = DaoUtils.like(detail);
        String paramsLike = DaoUtils.like(params);
        String usernameLike = DaoUtils.blankLike(username);

        final String sql = "SELECT COUNT(id) FROM l_operator " +
                "WHERE detail LIKE ? AND params LIKE ? AND username LIKE ? AND create_time BETWEEN ? AND ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{detailLike, paramsLike, usernameLike, fromDate, toDate}, Long.class);
    }

    public List<OptLog> find(String detail, String params, String username, Date fromDate, Date toDate, int offset, int limit){
        String detailLike = DaoUtils.like(detail);
        String paramsLike = DaoUtils.like(params);
        String usernameLike = DaoUtils.blankLike(username);

        final String sql = "SELECT * FROM l_operator " +
                "WHERE detail LIKE ? AND params LIKE ? AND username LIKE ? AND create_time BETWEEN ? AND ? " +
                "ORDER BY create_time DESC LIMIT ? OFFSET ?";

        return jdbcTemplate.query(sql, new Object[]{detailLike, paramsLike, usernameLike, fromDate, toDate, limit, offset}, mapper);
    }
}
