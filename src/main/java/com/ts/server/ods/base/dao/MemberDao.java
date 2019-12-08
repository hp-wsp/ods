package com.ts.server.ods.base.dao;

import com.ts.server.ods.base.domain.Member;
import com.ts.server.ods.common.utils.DaoUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

/**
 * 申报人员数据操作
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Repository
public class MemberDao {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Member> mapper = (r, i) -> {
        Member t = new Member();

        t.setId(r.getString("id"));
        t.setCompanyId(r.getString("company_id"));
        t.setUsername(r.getString("username"));
        t.setName(r.getString("name"));
        t.setPassword(r.getString("password"));
        t.setPhone(r.getString("phone"));
        t.setManager(r.getBoolean("is_manager"));
        t.setUpdateTime(r.getTimestamp("update_time"));
        t.setCreateTime(r.getTimestamp("create_time"));

        return t;
    };

    @Autowired
    public MemberDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void insert(Member t){
        final String sql = "INSERT INTO b_member (id, username, name, password, phone, company_id, is_manager, update_time, create_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, now(), now())";

        jdbcTemplate.update(sql, t.getId(), t.getUsername(), t.getName(), t.getPassword(), t.getPhone(), t.getCompanyId(), t.isManager());
    }

    public boolean update(Member t){
        final String sql = "UPDATE b_member SET name = ?, phone = ?, is_manager = ?, update_time = now() " +
                "WHERE id = ? AND is_delete = false";

        return jdbcTemplate.update(sql, t.getName(), t.getPhone(), t.isManager(), t.getId()) > 0;
    }

    public boolean notHasMember(String companyId){
        final String sql = "SELECT COUNT(id) FROM b_member WHERE company_id = ? AND is_delete = false";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{companyId}, Integer.class);
        return count != null && count == 0;
    }

    public boolean delete(String id){
        final String sql = "UPDATE b_member SET username = CONCAT(username,'@', ?), is_delete = true WHERE id = ?";

        String random = RandomStringUtils.randomAlphabetic(16);
        return jdbcTemplate.update(sql, random, id) > 0;
    }

    public void deleteMembers(String companyId){
        final String sql = "DELETE FROM b_member WHERE company_id = ?";
        jdbcTemplate.update(sql, companyId);
    }

    public boolean updatePassword(String id, String password){
        final String sql = "UPDATE b_member SET password = ? WHERE id = ? AND is_delete = false";
        return jdbcTemplate.update(sql, password, id) > 0;
    }

    public void cleanManager(String companyId){
        final String sql = "UPDATE b_member SET is_manage = ? WHERE company_id = ? AND is_delete = false";
        jdbcTemplate.update(sql, false, companyId);
    }

    public Member findOne(String id){
        final String sql = "SELECT * FROM b_member WHERE id = ? AND is_delete = false";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, mapper);
    }

    public Member findOneByUsername(String username){
        final String sql = "SELECT * FROM b_member WHERE username = ? AND is_delete = false";
        return jdbcTemplate.queryForObject(sql, new Object[]{username}, mapper);
    }

    public Member findManager(String companyId){
        final String sql = "SELECT * FROM b_member WHERE company_id = ? AND is_manager = true AND is_delete = false";
        return  jdbcTemplate.queryForObject(sql, new Object[]{companyId}, mapper);
    }

    public boolean hasUsername(String username){
        final String sql = "SELECT COUNT(id) FROM b_member WHERE username = ? AND is_delete = false";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{username}, Integer.class);

        return count != null && count > 0;
    }

    public List<Member> findByCompanyId(String companyId){
        final String sql = "SELECT * FROM b_member WHERE company_id = ? AND is_delete = false";
        return jdbcTemplate.query(sql, new Object[]{companyId}, mapper);
    }

    public Long count(String companyId, String username, String phone){
        final String sql = "SELECT COUNT(id) FROM b_member WHERE company_id LIKE ? AND username LIKE ? AND phone LIKE ? AND is_delete = false";

        String companyIdLike = DaoUtils.blankLike(companyId);
        String usernameLike = DaoUtils.like(username);
        String phoneLike = DaoUtils.like(phone);

        return jdbcTemplate.queryForObject(sql, new Object[]{companyIdLike, usernameLike, phoneLike}, Long.class);
    }

    public List<Member> find(String companyId, String username, String phone, int offset, int limit){
        final String sql = "SELECT * FROM b_member WHERE company_id LIKE ? AND username LIKE ? AND phone LIKE ? AND is_delete = false " +
                "ORDER BY create_time DESC LIMIT ? OFFSET ?";

        String companyIdLike = DaoUtils.blankLike(companyId);
        String usernameLike = DaoUtils.like(username);
        String phoneLike = DaoUtils.like(phone);

        return jdbcTemplate.query(sql, new Object[]{companyIdLike, usernameLike, phoneLike, limit, offset}, mapper);
    }
}
