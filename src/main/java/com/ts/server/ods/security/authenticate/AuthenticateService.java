package com.ts.server.ods.security.authenticate;

import com.ts.server.ods.security.authenticate.matcher.AntMatcher;
import com.ts.server.ods.security.authenticate.matcher.AuthenticateMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证业务服务
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Service
public class AuthenticateService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticateService.class);
	
	private final List<AuthenticateMatcher> matchers;
	private final boolean enable;

	@Autowired
	public AuthenticateService(AuthenticateProperties properties){
		LOGGER.info("Local security auth size is {}, matcher is {}",
                properties.getAuthentications().size(), properties.getMatcher());

		this.enable = properties.isEnable();
		this.matchers = properties.getAuthentications().stream()
				.flatMap(e -> e.getPatterns().stream().map(p -> new AntMatcher(p.getUri(), p.getMethods(), e.getRoles())))
				.collect(Collectors.toList());
	}
	
	public boolean authorization(String uri, String httpMethod, List<String> roles){
		return !enable || matchers.stream().anyMatch(e -> e.authorization(uri, httpMethod, roles));
	}
}
