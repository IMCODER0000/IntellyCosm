package gachonproject.web.session;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {


    public static final String SESSION_COOKIE_NAME = "mySessionId";
    private Map<String, Object> sessionsStore = new ConcurrentHashMap<>();


    //세션 생성
    public Cookie createSession(Object value, HttpServletResponse response) {


        //세션 id 생성, 값을 세션에 저장
        String sessionId = UUID.randomUUID().toString();
        sessionsStore.put(sessionId, value);

        //쿠키 생성
        Cookie mySessionCookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
        response.addCookie(mySessionCookie);



        return mySessionCookie;

    }

    //세션 조회
//    public Object getSession(HttpServletRequest request) {
//        Cookie[] cookies = request.getCookies();
//        if(cookies != null) {
//            return null;
//        }
//        for (Cookie cookie : cookies) {
//            if(cookie.getName().equals(SESSION_COOKIE_NAME)) {
//                return sessionsStore.get(cookie.getValue());
//            }
//        }
//        return null;
//    }
//

    //세션 조회
    public Object getSession(HttpServletRequest request) {
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if(sessionCookie == null){
            return null;
        }
        return sessionsStore.get(sessionCookie.getValue());
    }

    public Cookie findCookie(HttpServletRequest request, String cookieName) {
        if(request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findAny()
                .orElse(null);
    }

    //세션 만료
    public void expire(HttpServletRequest request) {
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if(sessionCookie != null){
            sessionsStore.remove(sessionCookie.getValue());
        }
    }


}
