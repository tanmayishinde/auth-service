package com.root.authservice.helpers;

import com.root.authservice.config.ConsulConfig;
import com.root.authservice.vo.UserVO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CookieHelper {
    private final JWTHelper jwtHelper;


    private final ConsulConfig config;

    @Autowired
    public CookieHelper(JWTHelper jwtHelper, ConsulConfig config) {
        this.jwtHelper = jwtHelper;
        this.config = config;
    }

    public void setCookie(UserVO userVO,
                          HttpServletResponse servletResponse, HttpServletRequest serverHttpRequest){

        int cookieTimeout = config.getConfigValueByKey("COOKIE_TIMEOUT", 1080);
        Cookie sessionCookie = null;
        Cookie[] cookies = serverHttpRequest.getCookies();
        for(Cookie cookie : cookies){
            if("session-id".equals(cookie.getName())){
                sessionCookie = cookie;
            }
        }
        servletResponse.addCookie(getSessionCookie(sessionCookie, cookieTimeout));
        servletResponse.addCookie(getJwtCookie(userVO, cookieTimeout));
    }

    private Cookie getSessionCookie(Cookie sessionCookie, int cookieTimeout){
        sessionCookie.setMaxAge(cookieTimeout);
        //sessionCookie.setSecure(true);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setDomain("localhost");
        sessionCookie.setPath("/");
        return sessionCookie;
    }
    private Cookie getJwtCookie(UserVO userVO, int cookieTimeout){
        Cookie jwtTokenCookie = new Cookie("auth", jwtHelper.getJwtToken(userVO));
        jwtTokenCookie.setMaxAge(cookieTimeout);
        //jwtTokenCookie.setSecure(true);
        jwtTokenCookie.setHttpOnly(true);
        jwtTokenCookie.setDomain("localhost");
        jwtTokenCookie.setPath("/");
        return jwtTokenCookie;
    }

}
