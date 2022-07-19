package Study.Board.interceptor;

import Study.Board.domain.Grade;
import Study.Board.domain.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;

public class GoldCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        if(session == null || session.getAttribute("loginUser") == null ||
                ((User) session.getAttribute("loginUser")).getGrade().compareTo(Grade.GOLD) < 0) {
            response.sendRedirect("/message?url=/&msg=" + URLEncoder.encode("골드 이상만 가능합니다!", "UTF-8"));
            return false;
        }
        return true;
    }
}
