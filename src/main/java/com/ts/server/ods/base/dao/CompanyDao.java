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
        t.setUpdateTime(r.getTimestamp("update_time"));
        t.setCreateTime(r.getTimestamp("create_time"));

        return t;
    };

    @Autowired
    public CompanyDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void insert(Company t){
        final String sql = "INSERT INTO b_company (id, name, phone, contact, update_time, create_time) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Date now = new Date();
        jdbcTemplate.update(sql, t.getId(), t.getName(), t.getPhone(),
                t.getContact(), DaoUtils.timestamp(now), DaoUtils.timestamp(now));
    }

    public boolean update(Company t){
        final String sql = "UPDATE b_company SET name = ?, phone = ?, contact = ?, update_time = now() " +
                "WHERE id = ? AND is_delete = false";
        return jdbcTemplate.update(sql, t.getName(), t.getPhone(), t.getContact(), t.getId()) > 0;
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
        final String sql = "SELECT * FROM b_company WHERE name LIKE ? AND is_delete = false ORDER BY create_time LIMIT ? OFFSET ?";

        String nameLike = DaoUtils.like(name);
        return jdbcTemplate.query(sql, new Object[]{nameLike, limit, offset}, mapper);
    }
}
