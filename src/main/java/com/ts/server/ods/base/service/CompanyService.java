package com.ts.server.ods.base.service;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.base.dao.CompanyDao;
import com.ts.server.ods.base.domain.Company;
import com.ts.server.ods.base.domain.Member;
import com.ts.server.ods.common.id.IdGenerators;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 单位业务服务
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Service
@Transactional(readOnly = true)
public class CompanyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyService.class);

    private final CompanyDao dao;
    private final MemberService memberService;

    @Autowired
    public CompanyService(CompanyDao dao, MemberService memberService) {
        this.dao = dao;
        this.memberService = memberService;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Company save(Company t){
        t.setId(IdGenerators.uuid());

        dao.insert(t);
        createMember(t);
        return dao.findOne(t.getId());
    }

    private void createMember(Company company){
        Member member = new Member();

        member.setUsername(company.getPhone());
        member.setCompanyId(company.getId());
        member.setName(company.getContact());
        member.setPhone(company.getPhone());
        member.setPassword(RandomStringUtils.random(8, "0123456789"));

        memberService.save(member);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Company update(Company t){
        Company o = get(t.getId());

        if(!dao.update(t)){
            throw new BaseException("修单位失败");
        }

        Company n = dao.findOne(o.getId());
        if(!StringUtils.equals(o.getPhone(), n.getPhone())){
            createMember(n);
        }

        return n;
    }

    public Company get(String id){
        try{
            return dao.findOne(id);
        }catch (DataAccessException e){
            LOGGER.error("Get company fail id={},throw={}", id, e.getMessage());
            throw new BaseException("单位不存在");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean delete(String id){
        return dao.delete(id);
    }

    public Long count(String name){
        return dao.count(name);
    }

    public List<Company> query(String name, int offset, int limit){
        return dao.find(name, offset, limit);
    }
}
