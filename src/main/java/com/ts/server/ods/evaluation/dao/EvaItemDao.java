package com.ts.server.ods.evaluation.dao;

import com.ts.server.ods.common.utils.DaoUtils;
import com.ts.server.ods.evaluation.domain.EvaItem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

/**
 * 指标数据操作
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Repository
public class EvaItemDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(EvaItemDao.class);

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<EvaItem> mapper = (r, i) -> {
        EvaItem t = new EvaItem();

        t.setId(r.getString("id"));
        t.setEvaId(r.getString("eva_id"));
        t.setNum(r.getString("eva_num"));
        t.setRequire(r.getString("require_content"));
        t.setGrade(r.getString("grade_content"));
        t.setResults(DaoUtils.toArray(r.getString("results")));
        t.setRemark(r.getString("remark"));
        t.setUpdateTime(r.getTimestamp("update_time"));
        t.setCreateTime(r.getTimestamp("create_time"));

        return t;
    };

    @Autowired
    public EvaItemDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void insert(EvaItem t){
        final String sql ="INSERT INTO e_item (id, eva_id, eva_num, format_num, require_content, grade_content, results, remark, " +
                "update_time, create_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, now(), now())";

        jdbcTemplate.update(sql, t.getId(), t.getEvaId(), t.getNum(), formatNum(t.getNum()), t.getRequire(), t.getGrade(),
                DaoUtils.join(t.getResults()), t.getRemark());
    }

    private String formatNum(String num){
        String[] array = StringUtils.split(num, "-");
        if(array.length == 1){
            return array[0];
        }
        array[1] = StringUtils.length(array[1]) > 1? array[1]: "0" + array[1];
        String format = StringUtils.join(array, "-");
        LOGGER.debug("Format num src={},target={}", num, format);

        return format;
    }

    public boolean update(EvaItem t){
        final String sql = "UPDATE e_item SET eva_num = ?, format_num = ?, require_content = ?, grade_content = ?, " +
                "results = ?, remark = ?, update_time = now() WHERE id = ?";
        return jdbcTemplate.update(sql, t.getNum(), formatNum(t.getNum()), t.getRequire(), t.getGrade(),
                DaoUtils.join(t.getResults()), t.getRemark(), t.getId()) > 0;
    }

    public boolean delete(String id){
        final String sql = "DELETE FROM e_item WHERE id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    public void deleteByEvaId(String evaId){
        final String sql = "DELETE FROM e_item WHERE eva_id = ?";
        jdbcTemplate.update(sql, evaId);
    }

    public EvaItem findOne(String id){
        final String sql = "SELECT * FROM e_item WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, mapper);
    }

    public EvaItem findOneByNum(String evaId, String num){
        final String sql = "SELECT * FROM e_item WHERE eva_id = ? AND eva_num = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{evaId, num}, mapper);
    }

    public boolean hasNumber(String evaId, String num){
        final String sql = "SELECT COUNT(id) FROM e_item WHERE eva_id = ? AND eva_num = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{evaId, num}, Integer.class);
        return count != null && count > 0;
    }

    public List<EvaItem> findByEvaId(String evaId){
        final String sql = "SELECT * FROM e_item WHERE eva_id = ?";
        return jdbcTemplate.query(sql, new Object[]{evaId}, mapper);
    }

    public Long count(String evaId, String num, String require){
        String evaIdLike = DaoUtils.blankLike(evaId);
        String numberLike = DaoUtils.like(num);
        String requireLike = DaoUtils.like(require);
        final String sql = "SELECT COUNT(id) FROM e_item WHERE eva_id LIKE ? AND eva_num LIKE ? AND require_content LIKE  ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{evaIdLike, numberLike, requireLike}, Long.class);
    }

    public List<EvaItem> find(String evaId, String num, String require, int offset, int limit){
        String evaIdLike = DaoUtils.blankLike(evaId);
        String numberLike = DaoUtils.like(num);
        String requireLike = DaoUtils.like(require);

        final String sql = "SELECT * FROM e_item WHERE eva_id LIKE ? AND eva_num LIKE ? AND require_content LIKE  ? ORDER BY format_num ASC LIMIT ? OFFSET ?";

        return jdbcTemplate.query(sql, new Object[]{evaIdLike, numberLike, requireLike, limit, offset}, mapper);
    }
}
