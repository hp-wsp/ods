package com.ts.server.ods.exec.dao;

import com.ts.server.ods.exec.domain.ExecLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

/**
 * 执行任务日志数据操作
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Repository
public class ExecLogDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<ExecLog> mapper = (r, i) -> {
        ExecLog t = new ExecLog();

        t.setId(r.getString("id"));
        t.setTaskKey(r.getString("task_key"));
        t.setRemark(r.getString("remark"));
        t.setStatus(ExecLog.Status.valueOf(r.getString("status")));
        t.setErrMsg(r.getString("err_msg"));
        t.setFromTime(r.getTimestamp("from_time"));
        t.setToTime(r.getTimestamp("to_time"));

        return t;
    };

    @Autowired
    public ExecLogDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void insert(String id, String taskKey, String remark){
        final String sql = "INSERT INTO l_exec (id, task_key, remark, status, from_time) VALUES (?, ?, ?, ?, now())";
        jdbcTemplate.update(sql, id, taskKey, remark, ExecLog.Status.RUNNING.name());
    }

    public boolean success(String id){
        final String sql = "UPDATE l_exec SET status = ?, to_time = now() WHERE id = ?";
        return jdbcTemplate.update(sql, ExecLog.Status.SUCCESS.name(), id) > 0;
    }

    public boolean fail(String id, String errMsg){
        final String sql = "UPDATE l_exec SET status = ?, err_msg = ?, to_time = now() WHERE id = ?";
        return jdbcTemplate.update(sql, ExecLog.Status.FAIL.name(),errMsg, id) > 0;
    }

    public ExecLog findOne(String id){
        final String sql = "SELECT * FROM l_exec WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, mapper);
    }

    public List<ExecLog> find(String taskKey){
        final String sql = "SELECT * FROM l_exec WHERE task_key = ? ORDER from_time DESC";
        return jdbcTemplate.query(sql, new Object[]{taskKey}, mapper);
    }
}
