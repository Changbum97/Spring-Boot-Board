package Study.Board;

import Study.Board.auth.PrincipalDetails;
import Study.Board.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.validation.constraints.Null;
import java.security.Principal;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if(principalDetails == null) {
            return "home";
        } else {
            User loginUser = principalDetails.getUser();
            model.addAttribute("loginUser", loginUser);
            return "loginHome";
        }
    }

    @GetMapping("/message")
    public String message( @RequestParam String msg, String url, Model model) {
        model.addAttribute("msg", msg);
        model.addAttribute("url", url);
        return "message";
    }
}
