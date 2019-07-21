package com.ts.server.ods.base.dao;

import com.ts.server.ods.base.domain.GradeRate;
import com.ts.server.ods.common.utils.DaoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

/**
 * 得分比率数据操作
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Repository
public class GradeRateDao {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<GradeRate> mapper = (r, i) -> {
        GradeRate t = new GradeRate();

        t.setId(r.getString("id"));
        t.setLevel(r.getString("level"));
        t.setRate(r.getInt("rate"));
        t.setUpdateTime(r.getTimestamp("update_time"));
        t.setCreateTime(r.getTimestamp("create_time"));

        return t;
    };

    @Autowired
    public GradeRateDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void insert(GradeRate t){
        final String sql = "INSERT INTO b_grade_rate (id, level, rate, update_time, create_time) VALUES (?, ?, ?, now(), now())";
        jdbcTemplate.update(sql, t.getId(), t.getLevel(), t.getRate());
    }

    public boolean update(GradeRate t){
        final String sql = "UPDATE b_grade_rate SET level = ?, rate =  ?, update_time = now() WHERE id = ?";
        return jdbcTemplate.update(sql, t.getLevel(), t.getRate(), t.getId()) >0;
    }

    public boolean delete(String id){
        final String sql = "DELETE FROM b_grade_rate WHERE id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    public boolean hasLevel(String level){
        final String sql = "SELECT COUNT(id) FROM b_grade_rate WHERE level = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{level}, Integer.class);
        return count != null && count > 0;
    }

    public GradeRate findOne(String id){
        final String sql = "SELECT * FROM b_grade_rate WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, mapper);
    }

    public List<GradeRate> findAll(){
        final String sql = "SELECT * FROM b_grade_rate ORDER BY level ASC";
        return jdbcTemplate.query(sql, mapper);
    }

    public Long count(String level){
        final String sql = "SELECT COUNT(id) FROM b_grade_rate WHERE level LIKE ?";
        String levelLike = DaoUtils.like(level);
        return jdbcTemplate.queryForObject(sql, new Object[]{levelLike}, Long.class);
    }

    public List<GradeRate> find(String level, int offset, int limit){
        final String sql = "SELECT * FROM b_grade_rate WHERE level LIKE ? ORDER BY level LIMIT ? OFFSET ?";
        String levelLike = DaoUtils.like(level);
        return jdbcTemplate.query(sql, new Object[]{levelLike, limit ,offset}, mapper);
    }
}
