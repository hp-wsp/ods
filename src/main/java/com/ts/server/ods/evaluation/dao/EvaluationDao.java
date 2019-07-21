package com.ts.server.ods.evaluation.dao;

import com.ts.server.ods.common.utils.DaoUtils;
import com.ts.server.ods.evaluation.domain.Evaluation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

/**
 * 测评数据操作
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Repository
public class EvaluationDao {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Evaluation> mapper = (r, i) -> {
        Evaluation t = new Evaluation();

        t.setId(r.getString("id"));
        t.setName(r.getString("name"));
        t.setRemark(r.getString("remark"));
        t.setFromTime(r.getTimestamp("from_time"));
        t.setToTime(r.getTimestamp("to_time"));
        t.setStatus(Evaluation.Status.valueOf(r.getString("status")));
        t.setExport(r.getBoolean("is_export"));
        t.setExportId(r.getString("export_id"));
        t.setSms(r.getBoolean("is_sms"));
        t.setUpdateTime(r.getTimestamp("update_time"));
        t.setCreateTime(r.getTimestamp("create_time"));

        return t;
    };

    @Autowired
    public EvaluationDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void insert(Evaluation t){
        final String sql = "INSERT INTO e_evaluation (id, name, remark, from_time, to_time, status, update_time, create_time)" +
                " VALUES (?, ?, ?, ?, ?, ?, now(), now())";
        jdbcTemplate.update(sql, t.getId(), t.getName(), t.getRemark(), t.getFromTime(), t.getToTime(), t.getStatus().name());
    }

    public boolean update(Evaluation t){
        final String sql ="UPDATE e_evaluation SET name = ?, remark = ?, from_time = ?, to_time = ?, " +
                "update_time = now() WHERE id = ?";
        return jdbcTemplate.update(sql, t.getName(), t.getRemark(), t.getFromTime(), t.getToTime(), t.getId()) > 0;
    }

    public boolean updateStatus(String id, Evaluation.Status status){
        final String sql = "UPDATE e_evaluation SET status =? WHERE id = ?";
        return jdbcTemplate.update(sql, status.name(), id) > 0;
    }

    public boolean updateSms(String id, boolean sms){
        final String sql = "UPDATE e_evaluation SET is_sms = ? WHERE id = ?";
        return jdbcTemplate.update(sql, sms, id) > 0;
    }

    public boolean delete(String id){
        final String sql = "DELETE FROM e_evaluation WHERE id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    public boolean updateExport(String id, String exportId){
        final String sql = "UPDATE e_evaluation SET is_export = ?, export_id = ? WHERE id = ?";
        return jdbcTemplate.update(sql, true, exportId,  id) > 0;
    }

    public Evaluation findOne(String id){
        final String sql = "SELECT * FROM e_evaluation WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, mapper);
    }

    public Optional<Evaluation> findLasted(){
        final String sql = "SELECT * FROM e_evaluation ORDER BY create_time DESC LIMIT 1";
        List<Evaluation> data = jdbcTemplate.query(sql, mapper);
        return data.isEmpty()? Optional.empty(): Optional.of(data.get(0));
    }

    public Long count(String name, Evaluation.Status status){
        String sql = "SELECT COUNT(id) FROM e_evaluation WHERE name LIKE ? AND status LIKE ? ";

        String nameLike = DaoUtils.like(name);
        String statusLike = status == null? "%": status.name();
        return jdbcTemplate.queryForObject(sql, new Object[]{nameLike, statusLike}, Long.class);
    }

    public List<Evaluation> find(String name, Evaluation.Status status, int offset, int limit){
        final String sql = "SELECT * FROM e_evaluation WHERE name LIKE ? AND status LIKE ? " +
                "ORDER BY create_time DESC LIMIT ? OFFSET ?";

        String nameLike = DaoUtils.like(name);
        String statusLike = status == null? "%": status.name();

        return  jdbcTemplate.query(sql, new Object[]{nameLike, statusLike, limit, offset}, mapper);
    }

    public List<Evaluation> findActiove(){
        final String sql = "SELECT * FROM e_evaluation WHERE status != 'CLOSE' ORDER BY create_time";
        return jdbcTemplate.query(sql, mapper);
    }
}
