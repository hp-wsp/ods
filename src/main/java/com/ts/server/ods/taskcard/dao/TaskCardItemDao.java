package com.ts.server.ods.taskcard.dao;

import com.ts.server.ods.common.utils.DaoUtils;
import com.ts.server.ods.taskcard.domain.TaskCardItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 任务卡指标数据操作
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Repository
public class TaskCardItemDao {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<TaskCardItem> mapper = new TaskItemMapper();

    @Autowired
    public TaskCardItemDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void insert(TaskCardItem t){
        final String sql = "INSERT INTO t_item (id, card_id, eva_item_id, eva_num, require_content, grade_content, remark, score, " +
                "result_labels, result_scores, show_order, update_time, create_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now(), now())";

        jdbcTemplate.update(sql, t.getId(), t.getCardId(), t.getEvaItemId(), t.getEvaNum(), t.getRequireContent(), t.getGradeContent(),
                t.getRemark(), t.getScore(), DaoUtils.join(buildResultLabels(t.getResults())), DaoUtils.join(buildResultScores(t.getResults())),
                t.getShowOrder());
    }

    private String[] buildResultLabels(List<TaskCardItem.TaskItemResult> results){
        return results.stream().map(TaskCardItem.TaskItemResult::getLevel).toArray(String[]::new);
    }

    private Integer[] buildResultScores(List<TaskCardItem.TaskItemResult> results){
        return results.stream().mapToInt(TaskCardItem.TaskItemResult::getScore).boxed().toArray(Integer[]::new);
    }

    public boolean update(TaskCardItem t){
        final String sql = "UPDATE t_item SET eva_item_id = ?, eva_num = ?, require_content = ?, grade_content = ?, remark = ?, score = ?," +
                "result_labels = ?, result_scores = ?, show_order = ?, update_time = now() WHERE id = ?";
        return jdbcTemplate.update(sql, t.getEvaItemId(), t.getEvaNum(), t.getRequireContent(), t.getGradeContent(), t.getRemark(), t.getScore(),
                DaoUtils.join(buildResultLabels(t.getResults())), DaoUtils.join(buildResultScores(t.getResults())), t.getShowOrder(), t.getId()) > 0;
    }

    public boolean delete(String id){
        final String sql = "DELETE FROM t_item WHERE id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    public void deleteByEvaId(String evaId){
        final String sql = "DELETE FROM t_item WHERE eva_item_id  = ?";
        jdbcTemplate.update(sql, evaId);
    }

    public void deleteByCardId(String cardId){
        final String sql = "DELETE FROM t_item WHERE card_id = ?";
        jdbcTemplate.update(sql, cardId);
    }

    public boolean grade(String id, String level, int score, String remark){
        final String sql = "UPDATE t_item SET grade_level =?, grade_score = ?, grade_remark = ? WHERE id = ?";
        return jdbcTemplate.update(sql, level, score, remark, id) >0;
    }

    public boolean updateDeclare(String id, boolean declare){
        final String sql = "UPDATE t_item SET is_declare = ? WHERE id = ?";
        return jdbcTemplate.update(sql, declare, id) > 0;
    }

    public TaskCardItem findOne(String id){
        final String sql = "SELECT * FROM t_item WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, mapper);
    }

    public List<TaskCardItem> findByCardId(String cardId){
        final String sql = "SELECT * FROM t_item WHERE card_id = ?";
        return jdbcTemplate.query(sql, new Object[]{cardId}, mapper);
    }

    public Integer countByCardId(String cardId){
        final String sql = "SELECT COUNT(id) FROM t_item WHERE card_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{cardId}, Integer.class);
    }

    public Integer countDecByCardId(String cardId){
        final String sql = "SELECT COUNT(id) FROM t_item WHERE card_id = ? AND is_declare = true";
        return jdbcTemplate.queryForObject(sql, new Object[]{cardId}, Integer.class);
    }

    public Long count(String cardId, String num, String require, String grade){
        final String sql = "SELECT COUNT(id) FROM t_item WHERE card_id LIKE ? AND eva_num LIKE ? " +
                "AND require_content LIKE ? AND grade_content LIKE ?";

        String taskIdLike = DaoUtils.blankLike(cardId);
        String numLike = DaoUtils.like(num);
        String requireLike = DaoUtils.like(require);
        String gradeLike = DaoUtils.like(grade);

        return jdbcTemplate.queryForObject(sql, new Object[]{taskIdLike, numLike, requireLike, gradeLike}, Long.class);
    }

    public List<TaskCardItem> find(String cardId, String num, String require, String grade, int offset, int limit){
        final String sql = "SELECT * FROM t_item WHERE card_id LIKE ? AND eva_num LIKE ? " +
                "AND require_content LIKE ? AND grade_content LIKE ? ORDER BY show_order, create_time LIMIT ? OFFSET ?";

        String taskIdLike = DaoUtils.blankLike(cardId);
        String numLike = DaoUtils.like(num);
        String requireLike = DaoUtils.like(require);
        String gradeLike = DaoUtils.like(grade);

        return jdbcTemplate.query(sql, new Object[]{taskIdLike, numLike, requireLike, gradeLike, limit, offset}, mapper);
    }

    static class TaskItemMapper implements RowMapper<TaskCardItem>{

        @Override
        public TaskCardItem mapRow(ResultSet rs, int rowNum) throws SQLException {
            TaskCardItem t = new TaskCardItem();

            t.setId(rs.getString("id"));
            t.setCardId(rs.getString("card_id"));
            t.setEvaItemId(rs.getString("eva_item_id"));
            t.setEvaNum(rs.getString("eva_num"));
            t.setRequireContent(rs.getString("require_content"));
            t.setGradeContent(rs.getString("grade_content"));
            t.setRemark(rs.getString("remark"));
            t.setScore(rs.getInt("score"));
            t.setDeclare(rs.getBoolean("is_declare"));
            t.setGrade(rs.getBoolean("is_grade"));
            t.setGradeLevel(rs.getString("grade_level"));
            t.setGradeScore(rs.getInt("grade_score"));
            t.setGradeRemark(rs.getString("grade_remark"));
            t.setResults(buildResults(rs));
            t.setShowOrder(rs.getInt("show_order"));
            t.setUpdateTime(rs.getTimestamp("update_time"));
            t.setCreateTime(rs.getTimestamp("create_time"));


            return t;
        }

        private List<TaskCardItem.TaskItemResult> buildResults(ResultSet rs) throws SQLException{
            String[] labels = DaoUtils.toArray(rs.getString("result_labels"));
            int[] scores = DaoUtils.toIntArray(rs.getString("result_scores"));

            if(labels.length == 0){
                return Collections.emptyList();
            }

            List<TaskCardItem.TaskItemResult> results = new ArrayList<>(labels.length);
            for(int i = 0; i < labels.length; i++){
                results.add(new TaskCardItem.TaskItemResult(labels[i], scores[i]));
            }

            return results;
        }
    }
}
