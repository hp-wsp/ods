package com.ts.server.ods.evaluation.dao;

import com.ts.server.ods.evaluation.domain.EvaluationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

/**
 * 评测任务操作日志数据操作
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Repository
public class EvaluationLogDao {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<EvaluationLog> mapper = (r, i) -> {
        EvaluationLog t = new EvaluationLog();

        t.setId(r.getString("id"));
        t.setEvaId(r.getString("eva_id"));
        t.setDay(r.getString("day"));
        t.setDetail(r.getString("detail"));
        t.setUsername(r.getString("username"));
        t.setCreateTime(r.getTimestamp("create_time"));

        return t;
    };

    @Autowired
    public EvaluationLogDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void insert(EvaluationLog t){
        final String sql = "INSERT l_evaluation (id, eva_id, day, detail, username, create_time) VALUES (?, ?, ?, ?, ?, now())";
        jdbcTemplate.update(sql, t.getId(), t.getEvaId(), t.getDay(), t.getDetail(), t.getUsername());
    }

    public List<EvaluationLog> find(String evaId){
        final String sql = "SELECT * FROM l_evaluation WHERE eva_id = ? ORDER BY create_time DESC";
        return jdbcTemplate.query(sql, new Object[]{evaId}, mapper);
    }
}
