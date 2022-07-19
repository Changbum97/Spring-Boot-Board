package Study.Board.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;

public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        if(session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect("/message?url=/user/login&msg=" + URLEncoder.encode("권한 없음", "UTF-8"));
            return false;
        }
        return true;
    }
}
