package com.ts.server.ods.etask.dao;

import com.ts.server.ods.etask.domain.Declaration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

/**
 * 任务指标申报材料数据操作
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Repository
public class DeclarationDao {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Declaration> mapper = (r, i) -> {
        Declaration t = new Declaration();

        t.setId(r.getString("id"));
        t.setCardId(r.getString("card_id"));
        t.setCardItemId(r.getString("item_id"));
        t.setEvaItemId(r.getString("eva_item_id"));
        t.setFileName(r.getString("file_name"));
        t.setFileSize(r.getInt("file_size"));
        t.setContentType(r.getString("content_type"));
        t.setPath(r.getString("path"));
        t.setDecUsername(r.getString("dec_username"));
        t.setCreateTime(r.getTimestamp("create_time"));

        return t;
    };

    @Autowired
    public DeclarationDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void insert(Declaration t){
        final String sql = "INSERT INTO t_declaration (id, card_id, item_id, eva_item_id, file_name, file_size, content_type, path, " +
                "dec_username, create_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
        jdbcTemplate.update(sql, t.getId(), t.getCardId(), t.getCardItemId(), t.getEvaItemId(), t.getFileName(), t.getFileSize(),
                t.getContentType(), t.getPath(), t.getDecUsername());
    }

    public boolean delete(String id){
        final String sql = "UPDATE t_declaration SET is_delete = ? WHERE id = ? AND is_delete = false";
        return jdbcTemplate.update(sql, true, id) > 0;
    }

    public void deleteByCardId(String cardId){
        final String sql = "UPDATE t_declaration SET is_delete = ? WHERE card_id = ? AND is_delete = false";
        jdbcTemplate.update(sql, true, cardId);
    }

    public Declaration findOne(String id){
        final String sql = "SELECT * FROM t_declaration WHERE id = ? AND is_delete = false";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, mapper);
    }

    public List<Declaration> findByCardId(String cardId){
        final  String sql = "SELECT * FROM t_declaration WHERE card_id = ? AND is_delete = false";
        return jdbcTemplate.query(sql, new Object[]{cardId}, mapper);
    }

    public List<Declaration> findByEvaItemId(String evaItemId){
        final String sql = "SELECT * FROM t_declaration WHERE eva_item_id = ? AND is_delete = false ORDER BY create_time";
        return jdbcTemplate.query(sql, new Object[]{evaItemId}, mapper);
    }

    public boolean hasByItemId(String itemId){
        final String sql = "SELECT COUNT(id) FROM t_declaration WHERE item_id = ? AND is_delete = false";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{itemId}, Integer.class);
        return count != null && count > 0;
    }

    public boolean hasByEvaItemId(String evaItemId){
        final String sql = "SELECT COUNT(id) FROM t_declaration WHERE eva_item_id = ? AND is_delete = false";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{evaItemId}, Integer.class);
        return count != null && count > 0;
    }

    public boolean hasByEvaId(String evaId){
        final String sql = "SELECT COUNT(id) FROM t_declaration WHERE card_id IN (SELECT id FROM t_card WHERE eva_id = ?) AND is_delete = false";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{evaId}, Integer.class);
        return count != null && count > 0;
    }

}
