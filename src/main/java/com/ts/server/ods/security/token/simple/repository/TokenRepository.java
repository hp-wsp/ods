package com.ts.server.ods.security.token.simple.repository;

import com.ts.server.ods.security.Credential;

import java.util.Optional;

/**
 * Token存储接口
 * 
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public interface TokenRepository {

	/**
	 * 保存新的token
     *
	 * @param token token值
	 * @param t 内容
     * @return true 保存成功
	 */
	boolean save(String token, Credential t);
	
	/**
	 * 得到token内容
	 * 
	 * @param token token值
	 * @return 内容
	 */
	Optional<Credential> get(String token);
	
	/**
	 * 删除token
	 * 
	 * @param token token值
	 * @return true 删除成功
	 */
	boolean remove(String token);

	/**
	 * 清理过期token
	 */
	void clear();
}
