package Study.Board.auth;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if(request.getRequestURI().equals("/content/1/write")) {
            response.sendRedirect("/message?url=/&msg=" + URLEncoder.encode("공지사항은 관리자만 작성 가능합니다!", "UTF-8"));
        } else {
            response.sendRedirect("/message?url=/&msg=" + URLEncoder.encode("골드이상만 접근 가능합니다!", "UTF-8"));
        }
    }
}
