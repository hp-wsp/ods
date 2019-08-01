package com.ts.server.ods.etask.dao;

import com.ts.server.ods.common.utils.DaoUtils;
import com.ts.server.ods.etask.domain.TaskCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 测评任务卡数据操作
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Repository
public class TaskCardDao {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<TaskCard> mapper = (r, i) -> {
        TaskCard t = new TaskCard();

        t.setId(r.getString("id"));
        t.setEvaId(r.getString("eva_id"));
        t.setEvaName(r.getString("eva_name"));
        t.setCompanyId(r.getString("company_id"));
        t.setCompanyName(r.getString("company_name"));
        t.setCompanyGroup(r.getString("company_group"));
        t.setCompanyGroupNum(r.getInt("company_group_num"));
        t.setAssId(r.getString("ass_id"));
        t.setAssUsername(r.getString("ass_username"));
        t.setAssName(r.getString("ass_name"));
        t.setDecId(r.getString("dec_id"));
        t.setDecUsername(r.getString("dec_username"));
        t.setDecName(r.getString("dec_name"));
        t.setOpen(r.getBoolean("is_open"));
        t.setStatus(TaskCard.Status.valueOf(r.getString("status")));
        t.setScore(r.getInt("score"));
        t.setGradeScore(r.getInt("grade_score"));
        t.setItemCount(r.getInt("item_count"));
        t.setDecCount(r.getInt("dec_count"));
        t.setUpdateTime(r.getTimestamp("update_time"));
        t.setCreateTime(r.getTimestamp("create_time"));

        return t;
    };

    @Autowired
    public TaskCardDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void insert(TaskCard t){
        final String sql = "INSERT INTO t_card (id, eva_id, eva_name, company_id, company_name, company_group, company_group_num, " +
                "ass_id, ass_username, ass_name, dec_id, dec_username, dec_name, is_open, status, update_time, create_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), now())";

        jdbcTemplate.update(sql, t.getId(), t.getEvaId(), t.getEvaName(), t.getCompanyId(), t.getCompanyName(),
                t.getCompanyGroup(), t.getCompanyGroupNum(), t.getAssId(), t.getAssUsername(), t.getAssName(),
                t.getDecId(), t.getDecUsername(), t.getDecName(), t.isOpen(), t.getStatus().name());
    }

    public boolean update(TaskCard t){
        final String sql = "UPDATE t_card SET company_id = ?, company_name = ?, company_group = ?, company_group_num = ?," +
                "ass_id = ?, ass_username = ?, ass_name = ?, dec_id = ?, dec_username = ?, dec_name = ?, update_time = now() " +
                "WHERE id = ?";

        return jdbcTemplate.update(sql, t.getCompanyId(), t.getCompanyName(), t.getCompanyGroup(), t.getCompanyGroupNum(),
                t.getAssId(), t.getAssUsername(), t.getAssName(), t.getDecId(), t.getDecUsername(), t.getDecName(), t.getId()) > 0;
    }

    public boolean delete(String id){
        final String sql = "DELETE FROM t_card WHERE id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    public boolean hasByEvaId(String evaId){
        final String sql = "SELECT COUNT(id) FROM t_card WHERE eva_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{evaId}, Integer.class);
        return count != null && count > 0;
    }

    public void updateOpen(String evaId, boolean open){
        final String sql = "UPDATE t_card SET is_open = ? WHERE eva_id = ?";
        jdbcTemplate.update(sql, open, evaId);
    }

    public boolean updateStatus(String id, TaskCard.Status status){
        final String sql = "UPDATE t_card SET status = ?, update_time = now() WHERE id = ?";
        return jdbcTemplate.update(sql, status.name(), id) > 0;
    }

    public boolean updateScore(String id, int score){
        final String sql = "UPDATE t_card SET score = ? WHERE id = ?";
        return jdbcTemplate.update(sql, score, id) > 0;
    }

    public boolean hasCompany(String evaId, String companyId){
        final String sql = "SELECT COUNT(id) FROM t_card WHERE eva_id = ? AND company_id = ?";
        Integer count =  jdbcTemplate.queryForObject(sql, new Object[]{evaId, companyId}, Integer.class);
        return count != null && count > 0;
    }

    public boolean updateGradeScore(String id, int gradeScore){
        final String sql = "UPDATE t_card SET grade_score = ? WHERE id = ?";
        return jdbcTemplate.update(sql, gradeScore, id) > 0;
    }

    public boolean updateItemCount(String id, int count){
        final String sql = "UPDATE t_card SET item_count = ? WHERE id = ?";
        return jdbcTemplate.update(sql, count, id) > 0;
    }

    public boolean updateDecCount(String id, int count) {
        final String sql = "UPDATE t_card SET dec_count = ? WHERE id = ?";
        return jdbcTemplate.update(sql, count, id) > 0;
    }

    public TaskCard findOne(String id){
        final String sql = "SELECT * FROM t_card WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, mapper);
    }

    public void updateDec(String evaId, String companyId, String userId, String username, String name){
        final String sql = "UPDATE t_card SET dec_id = ?, dec_username = ?, dec_name = ? WHERE eva_id = ? AND company_id = ?";
        jdbcTemplate.update(sql, userId, username, name, evaId, companyId);
    }

    public Long count(String evaId, String companyName, String assUsername, String decUsername){
        final String sql = "SELECT COUNT(id) FROM t_card WHERE eva_id LIKE ? AND company_name LIKE ? AND ass_username LIKE ? " +
                "AND dec_username LIKE ?";

        String evaIdLike = DaoUtils.blankLike(evaId);
        String companyNameLike = DaoUtils.like(companyName);
        String assUsernameLike = DaoUtils.like(assUsername);
        String decUsernameLike = DaoUtils.like(decUsername);

        return jdbcTemplate.queryForObject(sql, new Object[]{evaIdLike, companyNameLike, assUsernameLike, decUsernameLike}, Long.class);
    }

    public List<TaskCard> find(String evaId, String companyName, String assUsername, String decUsername, int offset, int limit){
        final String sql = "SELECT * FROM t_card WHERE eva_id LIKE ? AND company_name LIKE ? AND ass_username LIKE ? " +
                "AND dec_username LIKE ? ORDER BY company_group_num ASC, create_time ASC LIMIT ? OFFSET ?";


        String evaIdLike = DaoUtils.blankLike(evaId);
        String companyNameLike = DaoUtils.like(companyName);
        String assUsernameLike = DaoUtils.like(assUsername);
        String decUsernameLike = DaoUtils.like(decUsername);

        return jdbcTemplate.query(sql, new Object[]{evaIdLike, companyNameLike, assUsernameLike, decUsernameLike, limit, offset}, mapper);
    }

    public Long countOpenByAssId(String assId, String company){
        String assIdLike = DaoUtils.blankLike(assId);
        String companyLike = DaoUtils.like(company);
        final String sql = "SELECT COUNT(id) FROM t_card WHERE ass_id LIKE ? AND company_name LIKE ? AND is_open = TRUE ORDER BY create_time DESC";
        return jdbcTemplate.queryForObject(sql, new Object[]{assIdLike, companyLike}, Long.class);
    }

    public List<TaskCard> findOpenByAssId(String assId, String company, int offset, int limit){
        String assIdLike = DaoUtils.blankLike(assId);
        String companyLike = DaoUtils.like(company);
        final String sql = "SELECT * FROM t_card WHERE ass_id LIKE ? AND company_name LIKE ? AND is_open = TRUE " +
                "ORDER BY status ASC, update_time DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new Object[]{assIdLike, companyLike, limit, offset}, mapper);
    }

    public List<TaskCard> findOpenByDecId(String decId){
        final String sql = "SELECT * FROM t_card WHERE dec_id = ? AND is_open = TRUE ORDER BY create_time DESC";
        return jdbcTemplate.query(sql, new Object[]{decId}, mapper);
    }

    public List<TaskCard> findByEvaId(String evaId){
        final String sql = "SELECT * FROM t_card WHERE eva_id = ?";
        return jdbcTemplate.query(sql, new Object[]{evaId}, mapper);
    }

    public Long countGrade(String evaId, String companyName){
        return count(evaId, companyName, "", "");
    }

    public List<TaskCard> findGrade(String evaId, String companyName, int offset, int limit){
        final String sql = "SELECT * FROM t_card WHERE eva_id LIKE ? AND company_name LIKE ? ORDER BY grade_score DESC LIMIT ? OFFSET ?";

        String evaIdLike = DaoUtils.blankLike(evaId);
        String companyNameLike = DaoUtils.like(companyName);

        return jdbcTemplate.query(sql, new Object[]{evaIdLike, companyNameLike, limit, offset}, mapper);
    }

    public Map<String, Integer> findGroupStatus(String evaId){
        final String sql = "SELECT status, COUNT(id) count FROM t_card WHERE eva_id = ? GROUP BY status ORDER BY status";

        List<Object[]> data = jdbcTemplate.query(sql, new Object[]{evaId},
                (r, i) -> new Object[]{r.getString("status"), r.getInt("count")});

        return data.stream().collect(Collectors.toMap(e -> (String)e[0], e -> (Integer) e[1]));
    }
}
