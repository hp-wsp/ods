package com.ts.server.ods.base.service;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.base.dao.CompanyDao;
import com.ts.server.ods.base.dao.MemberDao;
import com.ts.server.ods.base.domain.Company;
import com.ts.server.ods.base.domain.Member;
import com.ts.server.ods.common.id.IdGenerators;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 申报员业务服务
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Service
@Transactional(readOnly = true)
public class MemberService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemberService.class);

    private final MemberDao dao;
    private final CompanyDao companyDao;

    @Autowired
    public MemberService(MemberDao dao, CompanyDao companyDao) {
        this.dao = dao;
        this.companyDao = companyDao;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Member save(Member t){
        if(dao.hasUsername(t.getUsername())){
            throw new BaseException("用户名已经存在");
        }

        Company company = getCompany(t.getCompanyId());

        //删除所有已经存在用户
        dao.findByCompanyId(company.getId()).forEach(e -> delete(e.getId()));

        t.setId(IdGenerators.uuid());
        t.setCompanyName(company.getName());
        dao.insert(t);

        return dao.findOne(t.getId());
    }

    private Company getCompany(String companyId){
        try{
            return companyDao.findOne(companyId);
        }catch (DataAccessException e){
            LOGGER.error("Get company fail id={},throw={}", companyId, e.getMessage());
            throw new BaseException("单位不存在");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Member update(Member t){

        Company company = getCompany(t.getCompanyId());
        t.setCompanyName(company.getName());
        if(!dao.update(t)){
            throw new BaseException("修改申报员失败");
        }

        return dao.findOne(t.getId());
    }

    public Member get(String id){
        try{
            return dao.findOne(id);
        }catch (DataAccessException e){
            throw new BaseException("申报员不存在");
        }
    }

    public Optional<Member> getValidate(String username, String password){
        try{
            Member m = dao.findOneByUsername(username);
            return StringUtils.equals(m.getPassword(), password)? Optional.of(m): Optional.empty();
        }catch (Exception e){
            LOGGER.error("Get member username={},throw={}", username, e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<Member> getUsername(String username){
        try{
            return Optional.of(dao.findOneByUsername(username));
        }catch (Exception e){
            return Optional.empty();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean delete(String id){
        return dao.delete(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean updatePassword(String id, String password, String newPassword){
        Member o = get(id);

        if(!StringUtils.equals(o.getPassword(), password)){
            throw new BaseException("密码错误");
        }

        return dao.updatePassword(id, newPassword);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean resetPassword(String id, String newPassword){
        return dao.updatePassword(id, newPassword);
    }

    public Long count(String companyId, String companyName, String username, String phone){
        return dao.count(companyId, companyName, username, phone);
    }

    public List<Member> query(String companyId, String companyName, String username, String phone, int offset, int limit){
        return dao.find(companyId, companyName, username, phone, offset, limit);
    }

    public List<Member> queryByCompanyId(String companyId){
        return dao.findByCompanyId(companyId);
    }
}
