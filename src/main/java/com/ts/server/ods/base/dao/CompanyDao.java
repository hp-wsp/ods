package com.ts.server.ods.base.dao;

import com.ts.server.ods.base.domain.Company;
import com.ts.server.ods.common.utils.DaoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

/**
 * 单位数据操作
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Repository
public class CompanyDao {
    private final JdbcTemplate jdbcTemplate;

    private RowMapper<Company> mapper = (r, i) -> {
        Company t = new Company();

        t.setId(r.getString("id"));
        t.setName(r.getString("name"));
        t.setPhone(r.getString("phone"));
        t.setContact(r.getString("contact"));
        t.setGroup(r.getString("c_group"));
        t.setGroupNum(r.getInt("c_group_num"));
        t.setUpdateTime(r.getTimestamp("update_time"));
        t.setCreateTime(r.getTimestamp("create_time"));

        return t;
    };

    @Autowired
    public CompanyDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void insert(Company t){
        final String sql = "INSERT INTO b_company (id, name, phone, contact, c_group, c_group_num, update_time, create_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, now(), now())";

        Date now = new Date();
        jdbcTemplate.update(sql, t.getId(), t.getName(), t.getPhone(), t.getContact(), t.getGroup(), t.getGroupNum());
    }

    public boolean update(Company t){
        final String sql = "UPDATE b_company SET name = ?, phone = ?, contact = ?, c_group = ?, c_group_num = ?, update_time = now() " +
                "WHERE id = ? AND is_delete = false";
        return jdbcTemplate.update(sql, t.getName(), t.getPhone(), t.getContact(), t.getGroup(), t.getGroupNum(), t.getId()) > 0;
    }

    public Company findOne(String id){
        final String sql = "SELECT * FROM b_company WHERE id =? AND is_delete = false";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, mapper);
    }

    public boolean delete(String id){
        final String sql = "UPDATE b_company SET is_delete = true WHERE id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    public Long count(String name){
        final String sql = "SELECT COUNT(id) FROM b_company WHERE name LIKE ? AND is_delete = false";

        String nameLike = DaoUtils.like(name);
        return jdbcTemplate.queryForObject(sql, new Object[]{nameLike}, Long.class);
    }

    public List<Company> find(String name, int offset, int limit){
        final String sql = "SELECT * FROM b_company WHERE name LIKE ? AND is_delete = false " +
                "ORDER BY c_group_num ASC, create_time ASC LIMIT ? OFFSET ?";

        String nameLike = DaoUtils.like(name);
        return jdbcTemplate.query(sql, new Object[]{nameLike, limit, offset}, mapper);
    }

    public List<Company> findNotAss(String evaId, String name){
        String nameLike = DaoUtils.like(name);
        final String sql = "SELECT * FROM b_company " +
                "WHERE id NOT IN (SELECT company_id FROM t_card WHERE eva_id = ?) AND name LIKE ? AND is_delete = false " +
                "ORDER BY c_group_num ASC, create_time ASC";
        return jdbcTemplate.query(sql, new Object[]{evaId, nameLike}, mapper);
    }
}
