package Study.Board.controller;

import Study.Board.domain.*;
import Study.Board.repository.UserLikeRepository;
import Study.Board.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/user")
@Controller
@Slf4j
public class UserController {

    public static Hashtable sessionList = new Hashtable();
    private final UserService userService;
    private final UserLikeRepository userLikeRepository;

    @GetMapping("/signup")
    public String signupForm(@ModelAttribute UserSignupForm form) {
        return "user/signupForm";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute UserSignupForm form, BindingResult bindingResult,
                         HttpServletRequest request, Model model) {
        if (userService.findByLoginId(form.getLoginId()).isPresent()) {
            bindingResult.rejectValue("loginId", "signupFail", "아이디가 중복됩니다!");
        }
        if (userService.findByNickname(form.getNickname()).isPresent()) {
            bindingResult.rejectValue("nickname", "signupFail", "닉네임이 중복됩니다!");
        }
        if (!form.getPassword().equals(form.getPasswordCheck())) {
            bindingResult.rejectValue("passwordCheck", "signupFail", "비밀번호가 일치하지 않습니다!");
        }
        if (bindingResult.hasErrors()) {
            log.info("회원가입 실패");
            return "user/signupForm";
        }

        User user = new User();
        user.setLoginId(form.getLoginId());
        user.setNickname(form.getNickname());
        user.setPassword(form.getPassword());
        user.setGrade(Grade.BRONZE);
        user.setSignupDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        user.setLikeCount(0);
        userService.signup(user);
        log.info("회원가입 성공 : {}", user.getNickname());

        HttpSession session = request.getSession(true);
        session.setAttribute("loginUser", user);
        sessionList.put(session.getId(), session);

        log.info("로그인 성공 : {}", user.getNickname());
        model.addAttribute("msg", "회원가입 + 로그인 성공!");
        model.addAttribute("url", "/");
        return "message";
    }

    @GetMapping("/login")
    public String loginForm(@ModelAttribute UserLoginForm form) {
        return "user/loginForm";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute UserLoginForm form, BindingResult bindingResult,
                        HttpServletRequest request, Model model) {
        Optional<User> optionalUser = userService.findByLoginId(form.getLoginId());
        if (optionalUser.isEmpty()) {
            bindingResult.rejectValue("loginId", "loginFail", "유저가 존재하지 않습니다!");
            return "user/loginForm";
        }
        User user = optionalUser.get();
        if (!user.getPassword().equals(form.getPassword())) {
            bindingResult.rejectValue("password", "loginFail", "비밀번호가 일치하지 않습니다!");
            return "user/loginForm";
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("loginUser", user);
        sessionList.put(session.getId(), session);

        log.info("로그인 성공 : {}", user.getNickname());
        model.addAttribute("msg", "로그인 성공!");
        model.addAttribute("url", "/");
        return "message";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            sessionList.remove(session.getId());
            session.invalidate();
        }
        log.info("로그아웃 성공");
        model.addAttribute("msg", "로그아웃 되었습니다!");
        model.addAttribute("url", "/");
        return "message";
    }

    @GetMapping("/edit")
    public String editForm(@SessionAttribute(name = "loginUser") User loginUser, Model model) {
        UserEditForm form = new UserEditForm();
        form.setLoginId(loginUser.getLoginId());
        form.setOldNickname(loginUser.getNickname());
        model.addAttribute("userEditForm", form);
        return "user/editForm";
    }

    @PostMapping("/edit")
    public String edit(@SessionAttribute(name = "loginUser") User loginUser,
                       @Valid @ModelAttribute UserEditForm form, BindingResult bindingResult,
                       HttpServletRequest request, Model model) {
        if (!loginUser.getPassword().equals(form.getOldPassword())) {
            bindingResult.rejectValue("oldPassword", "editFail", "현재 비밀번호를 틀렸습니다!");
        }
        if (!form.getPassword().equals(form.getPasswordCheck())) {
            bindingResult.rejectValue("passwordCheck", "editFail", "비밀번호가 일치하지 않습니다!");
        }
        if (userService.findByNickname(form.getNickname()).isPresent() && !loginUser.getNickname().equals(form.getNickname())) {
            bindingResult.rejectValue("nickname", "editFail", "닉네임이 중복됩니다!");
        }
        if (bindingResult.hasErrors()) {
            log.info("정보수정 실패");
            return "user/editForm";
        }

        User user = userService.findById(loginUser.getId());

        HttpSession oldSession = request.getSession(false);
        sessionList.remove(oldSession.getId());
        oldSession.invalidate();

        user.setGrade(loginUser.getGrade());
        user.setNickname(form.getNickname());
        user.setPassword(form.getPassword());
        userService.edit(user);

        HttpSession session = request.getSession(true);
        session.setAttribute("loginUser", user);
        sessionList.put(session.getId(), session);

        log.info("정보수정 성공 : {}", user.getNickname());
        model.addAttribute("msg", "정보수정 성공!");
        model.addAttribute("url", "/");
        return "message";
    }

    @GetMapping("/list")
    public String userList(Model model, @RequestParam(required = false, defaultValue = "1") int page) {
        model.addAttribute("users", userService.findAll(page - 1));
        if (page == 1) {
            model.addAttribute("hasPreviousPage", false);
        } else {
            model.addAttribute("hasPreviousPage", true);
        }

        if (userService.findAll(page).isEmpty()) {
            model.addAttribute("hasNextPage", false);
        } else {
            model.addAttribute("hasNextPage", true);
        }
        model.addAttribute("lastPage", userService.findLastPage());
        model.addAttribute("nowPage", page);

        return "user/userList";
    }

    @GetMapping("/delete")
    public String deleteForm(@ModelAttribute UserLoginForm form) {
        return "user/deleteForm";
    }

    @PostMapping("/delete")
    public String delete(@ModelAttribute UserLoginForm form, Model model, HttpServletRequest request,
                         @SessionAttribute(name = "loginUser") User loginUser) {
        if (!form.getPassword().equals(loginUser.getPassword())) {
            model.addAttribute("msg", "비밀번호가 틀렸습니다!");
            model.addAttribute("url", "/user/delete");
            log.info("회원탈퇴 실패");
            return "message";
        } else {
            userService.delete(loginUser.getId());
            log.info("회원탈퇴 성공");
            HttpSession session = request.getSession(false);
            if (session != null) {
                sessionList.remove(session.getId());
                session.invalidate();
            }
            model.addAttribute("msg", "회원탈퇴 되었습니다!");
            model.addAttribute("url", "/");
            return "message";
        }
    }
}
 /*   @PostConstruct
    public void init() {
        User admin = new User();
        admin.setLoginId("admin");
        admin.setNickname("관리자");
        admin.setGrade(Grade.ADMIN);
        admin.setPassword("1234");
        admin.setSignupDate((LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        admin.setLikeCount(0);
        userService.signup(admin);

        User user1 = new User();
        user1.setLoginId("chb2005");
        user1.setNickname("창범");
        user1.setGrade(Grade.GOLD);
        user1.setPassword("1234");
        user1.setSignupDate((LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        user1.setLikeCount(0);
        userService.signup(user1);

        User[] users = new User[30];
        for(int i = 1 ; i <= 25 ; i ++) {
            users[i] = new User();
            users[i].setLoginId("user" + i);
            users[i].setNickname("유저" + i);
            users[i].setPassword("1234");
            users[i].setGrade(Grade.BRONZE);
            users[i].setSignupDate((LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            users[i].setLikeCount(0);
            userService.signup(users[i]);
        }
    }
}*/
