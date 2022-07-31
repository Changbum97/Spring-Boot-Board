package Study.Board.config;

import Study.Board.auth.CustomAccessDeniedHandler;
import Study.Board.auth.CustomAuthenticationEntryPoint;
import Study.Board.auth.LoginFailureHandler;
import Study.Board.oauth.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.net.URLEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PrincipalOauth2UserService principalOauth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/content/1/write").access("hasAuthority('ADMIN')")
                .antMatchers("/content/list/3/**", "/content/3/write")
                .access("hasAuthority('GOLD') or hasAuthority('ADMIN')")
                .antMatchers("/", "/mycss.css", "/user/login", "/user/signup", "/message", "/error",
                        "/oauth2/**")
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .usernameParameter("loginId")
                .loginPage("/user/login")
                .loginProcessingUrl("/user/login")
                .defaultSuccessUrl("/message?url=/&msg="+ URLEncoder.encode("로그인에 성공했습니다!", "UTF-8"))
                .failureHandler(new LoginFailureHandler())
                .and()
                .oauth2Login()
                .loginPage("/user/login")
                .defaultSuccessUrl("/message?url=/&msg="+ URLEncoder.encode("로그인에 성공했습니다!", "UTF-8"))
                .userInfoEndpoint()
                .userService(principalOauth2UserService);
        http.exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler());

    }
}
