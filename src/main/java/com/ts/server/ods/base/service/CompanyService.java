package com.ts.server.ods.base.service;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.base.dao.CompanyDao;
import com.ts.server.ods.base.domain.Company;
import com.ts.server.ods.common.id.IdGenerators;
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
        return dao.findOne(t.getId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Company update(Company t){
        if(!dao.update(t)){
            throw new BaseException("修单位失败");
        }

        return get(t.getId());
    }

    public Company get(String id){
        try{
            return dao.findOne(id);
        }catch (DataAccessException e){
            LOGGER.error("Get company fail id={},throw={}", id, e.getMessage());
            throw new BaseException("单位不存在");
        }
    }

    public boolean has(String id){
        return dao.has(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean delete(String id){
        boolean ok = dao.delete(id);
        if(ok){
            memberService.deleteMembers(id);
        }
        return ok;
    }

    public Long count(String name){
        return dao.count(name);
    }

    public List<Company> query(String name, int offset, int limit){
        return dao.find(name, offset, limit);
    }

    public List<Company> queryNotAss(String evaId, String name){
        return dao.findNotAss(evaId, name);
    }
}
