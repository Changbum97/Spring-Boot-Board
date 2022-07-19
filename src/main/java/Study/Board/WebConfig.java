package Study.Board;

import Study.Board.interceptor.AdminCheckInterceptor;
import Study.Board.interceptor.GoldCheckInterceptor;
import Study.Board.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/mycss.css", "/user/login", "/user/signup", "/message");
        registry.addInterceptor(new AdminCheckInterceptor())
                .order(2)
                .addPathPatterns("/content/1/write");
        registry.addInterceptor(new GoldCheckInterceptor())
                .order(3)
                .addPathPatterns("/content/list/3/**", "/content/3/write");
    }
}
