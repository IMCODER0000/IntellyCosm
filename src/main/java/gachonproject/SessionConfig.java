package gachonproject;


import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
@EnableAutoConfiguration
public class SessionConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("MY_SESSION_COOKIE"); // 세션 쿠키의 이름을 설정합니다.
        serializer.setCookiePath("/"); // 쿠키의 경로를 설정합니다.
        serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$"); // 도메인 패턴을 설정합니다. 필요에 따라 변경할 수 있습니다.
        serializer.setUseSecureCookie(true); // HTTPS를 통해서만 쿠키가 전송되도록 설정합니다.
        return serializer;
    }
}
