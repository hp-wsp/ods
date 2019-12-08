package com.ts.server.ods.base.service;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.base.dao.MemberDao;
import com.ts.server.ods.base.domain.Member;
import com.ts.server.ods.base.event.MemberEvent;
import com.ts.server.ods.common.id.IdGenerators;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher publisher;

    @Autowired
    public MemberService(MemberDao dao, ApplicationEventPublisher publisher) {
        this.dao = dao;
        this.publisher = publisher;
    }

    /**
     * 新增申报员
     *
     * @param t {@link Member}
     * @return {@link Member}
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Member save(Member t){
        if(dao.hasUsername(t.getUsername())){
            throw new BaseException("用户名已经存在");
        }

        t.setId(IdGenerators.uuid());
        t.setManager(dao.notHasMember(t.getCompanyId()));
        dao.insert(t);

        return dao.findOne(t.getId());
    }

    /**
     * 修改申报员信息
     *
     * @param t {@link Member}
     * @return {@link Member}
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Member update(Member t){
        Member o = dao.findOne(t.getId());

        if(!o.isManager() && t.isManager()){
            dao.cleanManager(t.getCompanyId());
        }

        if(!dao.update(t)){
            throw new BaseException("修改申报员失败");
        }

        if(!StringUtils.equals(t.getUsername(), o.getUsername()) ||
                !StringUtils.equals(t.getName(), o.getName())){
            publisher.publishEvent(new MemberEvent(t.getId(), "update"));
        }

        return dao.findOne(t.getId());
    }

    /**
     * 更新申报员为单位管理员
     *
     * @param id 申报员编号
     * @return {@link Member}
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Member activeManage(String id){
        Member t = get(id);
        if(t.isManager()){
            return t;
        }

        dao.cleanManager(t.getCompanyId());
        t.setManager(true);
        return update(t);
    }

    /**
     * 得到申报员信息
     *
     * @param id 申报员编号
     * @return {@link Member}
     */
    public Member get(String id){
        try{
            return dao.findOne(id);
        }catch (DataAccessException e){
            throw new BaseException("申报员不存在");
        }
    }

    /**
     * 获取单位管理员
     *
     * @param companyId 单位编号
     * @return 单位管理员
     */
    public Member getManager(String companyId){
        try{
            return dao.findManager(companyId);
        }catch (DataAccessException e){
            throw new BaseException("单位管理员不存在");
        }
    }

    /**
     * 验证申报员密码是否正确
     *
     * @param username 用户名
     * @param password 密码
     * @return 验证成功返回申报员信息
     */
    public Optional<Member> getValidate(String username, String password){
        try{
            Member m = dao.findOneByUsername(username);
            return StringUtils.equals(m.getPassword(), password)? Optional.of(m): Optional.empty();
        }catch (Exception e){
            LOGGER.error("Get member username={},throw={}", username, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 通过用户名查询申报员
     *
     * @param username 用户名
     * @return {@link Member}
     */
    public Optional<Member> getUsername(String username){
        try{
            return Optional.of(dao.findOneByUsername(username));
        }catch (Exception e){
            return Optional.empty();
        }
    }

    /**
     * 删除申报员
     *
     * @param id 编号
     * @return true:删除成功
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean delete(String id){
        Member t = get(id);
        if(t.isManager()){
            throw new BaseException("管理员不能删除");
        }

        publisher.publishEvent(new MemberEvent(id, "delete"));

        return dao.delete(id);
    }

    /**
     * 删除单位内申报员
     *
     * @param companyId 单位编号
     */
    public void deleteMembers(String companyId){
        dao.deleteMembers(companyId);
    }

    /**
     * 修改密码
     *
     * @param id 申报员编号
     * @param password 老密码
     * @param newPassword 新密码
     * @return true:修改成功
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean updatePassword(String id, String password, String newPassword){
        Member o = get(id);

        if(!StringUtils.equals(o.getPassword(), password)){
            throw new BaseException("密码错误");
        }

        return dao.updatePassword(id, newPassword);
    }

    /**
     * 重置密码
     *
     * @param id 申报员密码
     * @param newPassword 新密码
     * @return true:重置成功
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean resetPassword(String id, String newPassword){
        return dao.updatePassword(id, newPassword);
    }

    /**
     * 查询申报员记录数
     *
     * @param companyId 公司编号
     * @param username 用户名
     * @param phone 联系电话
     * @return 记录数
     */
    public Long count(String companyId, String username, String phone){
        return dao.count(companyId, username, phone);
    }

    /**
     * 查询申报员
     *
     * @param companyId 公司编号
     * @param username 用户名
     * @param phone 联系电话
     * @param offset 查询开始位置
     * @param limit 查询条数
     * @return 申报员集合
     */
    public List<Member> query(String companyId, String username, String phone, int offset, int limit){
        return dao.find(companyId, username, phone, offset, limit);
    }

    public List<Member> queryByCompanyId(String companyId){
        return dao.findByCompanyId(companyId);
    }
}
