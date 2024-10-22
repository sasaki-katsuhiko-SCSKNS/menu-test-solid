package com.blank.web.app.config.auth;

import com.ymsl.solid.base.constants.BaseConstants;
import com.ymsl.solid.base.json.JsonUtils;
import com.ymsl.solid.base.json.exception.JsonOperationException;
import com.ymsl.solid.context.auth.BaseUserDetailsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Map;

/**
 * User details service for project.
 * Load user details from database or other sources.
 * Generate temp user details for password validation via {@link #loadUserPwdByUsername(String)},
 * and create complete user details via {@link #loadUserByUsername(String)}.
 */
public class PJUserDetailsServiceLoader implements BaseUserDetailsService {

  @Override
  public UserDetails loadUserByUsername(String userLoginInfo) throws UsernameNotFoundException {

    String userCode = "";
    try {
      Map<String, String> loginInfo = JsonUtils.toMap(userLoginInfo, String.class, String.class);
      // get user code from json format userLoginInfo
      userCode = loginInfo.get(BaseConstants.UserInfo.USER_NAME);
    } catch (JsonOperationException e) {
      // userLoginInfo is not json, so it can be used as user code
      userCode = userLoginInfo;
    }

    // role information and org information can be loaded from database
    // role should be prefixed with "ROLE_" for security configuration
    List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
    if(userCode.equalsIgnoreCase("ADMIN")) {
      authorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");
    }

    // reserve the userLoginInfo in case you want to use jwttoken or remember-me service
    PJUserDetails user = new PJUserDetails(userLoginInfo, "", authorities);
    user.setUserCode(userCode);

    user.setCompanyCode("0000");
    return user;
  }

  @Override
  public UserDetails loadUserPwdByUsername(String userLoginInfo) throws UsernameNotFoundException {

    String userName = "";

    try {
      Map<String, String> loginInfo = JsonUtils.toMap(userLoginInfo, String.class, String.class);
      if (loginInfo == null || StringUtils.isEmpty(loginInfo.get("username"))) {
        throw new UsernameNotFoundException("no user info");
      }
      userName = loginInfo.get(BaseConstants.UserInfo.USER_NAME);
    } catch (JsonOperationException e) {
      // userLoginInfo is not json, so it can be used as user code
      if (StringUtils.isEmpty(userLoginInfo)) {
        throw new UsernameNotFoundException("no user info");
      }
      userName = userLoginInfo;
    }

    // encoded for user
    String passwordFromDb = "$2a$10$m877TxuLQyUExu6A3CdCCucwjCaudtLZwiujzC5ExhpYb/Sbw/gXa";
    //
    return new User(userName, passwordFromDb, AuthorityUtils.NO_AUTHORITIES);
  }

}
