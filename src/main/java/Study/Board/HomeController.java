package Study.Board;

import Study.Board.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.validation.constraints.Null;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@SessionAttribute(name = "loginUser", required = false) User loginUser, Model model){
        if(loginUser == null) {
            return "home";
        } else {
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
