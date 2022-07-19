package Study.Board.controller;

import Study.Board.domain.*;
import Study.Board.repository.ContentRepository;
import Study.Board.repository.UploadFileRepository;
import Study.Board.repository.UserLikeRepository;
import Study.Board.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.core.io.Resource;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/content")
public class ContentController {
    
    private final ContentRepository contentRepository;
    private final UserService userService;
    private final UserLikeRepository userLikeRepository;
    private final FileStore fileStore;
    private final UploadFileRepository uploadFileRepository;

    @PostMapping("/search")
    public String search(HttpServletRequest request, @ModelAttribute SearchForm form) throws UnsupportedEncodingException {
        char from = request.getHeader("REFERER").toString().split("/")
                [request.getHeader("REFERER").toString().split("/").length - 1].charAt(0);
        return "redirect:/content/list/"+from+"?page=1&opt="+form.getOpt()+"&str="
                + URLEncoder.encode(form.getStr(),"UTF-8");
    }

    @GetMapping("/list/{category}")
    public String showContentList(Model model, @RequestParam(required = false, defaultValue = "1") int page,
                                  @PathVariable int category, @SessionAttribute(name = "loginUser") User loginUser,
                                  @RequestParam(required = false, defaultValue = "0") int opt,
                                  @RequestParam(required = false, defaultValue = "") String str){
        PageRequest pageRequest = PageRequest.of(page - 1,10);

        log.info("============");
        log.info("opt : {}", opt);
        log.info("str : {}", str);

        Page<Content> result;

        if(opt == 1) {
            if(category == 4) {
                result = userLikeRepository.findByUserIdAndContentTitleContains(loginUser.getId(), str, pageRequest);
            } else {
                result = contentRepository.findByCategoryAndTitleContains(category, str, pageRequest);
            }
        } else if(opt == 2) {
            if(category == 4) {
                result = userLikeRepository.findByUserIdAndUserNickname(loginUser.getId(), str, pageRequest);
            } else {
                result = contentRepository.findByCategoryAndUserNickname(category, str, pageRequest);
            }
        } else {    // 정렬
            if(opt == 3) {
                pageRequest = PageRequest.of(page -1, 10, Sort.Direction.DESC, "likeCount");
            } else if(opt == 4) {
                pageRequest = PageRequest.of(page -1, 10, Sort.Direction.DESC, "uploadDate");
            }

            if(category == 4) {
                result = userLikeRepository.findByUserId(loginUser.getId(), pageRequest);
            } else {
                result = contentRepository.findByCategory(category, pageRequest);
            }
        }
        model.addAttribute("contentList", result.getContent());
        if(result.getTotalPages() == 0) {
            model.addAttribute("totalPages", 1);
        } else {
            model.addAttribute("totalPages", result.getTotalPages());
        }
        model.addAttribute("hasNextPage", result.hasNext());
        model.addAttribute("hasPreviousPage", result.hasPrevious());

        model.addAttribute("opt", opt);
        model.addAttribute("str", str);
        model.addAttribute("category", category);
        model.addAttribute("page", page);
        model.addAttribute("searchForm", new SearchForm());

        return "content/list";
    }

    @GetMapping("/{category}/write")
    public String writeForm(Model model, @PathVariable int category, @SessionAttribute(name = "loginUser") User loginUser){
        ContentWriteForm form = new ContentWriteForm();
        form.setCategory(category);
        form.setWriter(loginUser.getNickname());
        model.addAttribute("contentWriteForm", form);
        return "content/writeForm";
    }

    @PostMapping("/{category}/write")
    public String write(@Valid @ModelAttribute ContentWriteForm form, BindingResult bindingResult,
                        @SessionAttribute(name = "loginUser") User loginUser, Model model) throws IOException {
        if(form.getImageFiles().stream()
                .filter(f -> !f.getContentType().startsWith("image"))
                .findAny()
                .isPresent() && form.getImageFiles().get(0).getSize() != 0) {
            log.info("이미지가 아님!");
            bindingResult.rejectValue("imageFiles", "writeFail", "이미지가 아닙니다!");
        }


        if(bindingResult.hasErrors()) {
            log.info("글 작성 실패");
            form.setWriter(loginUser.getNickname());
            return "content/writeForm";
        }

        Content content = new Content();
        content.setTitle(form.getTitle());
        content.setTexts(form.getTexts());
        content.setCategory(form.getCategory());
        content.setLikeCount(0);
        content.setUser(userService.findById(loginUser.getId()));
        content.setUploadDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Content content1 = contentRepository.save(content);

        List<UploadFile> imageFiles = fileStore.storeFiles(form.getImageFiles(), content1);
        content.setImageFiles(imageFiles);
        contentRepository.save(content1);

        if(loginUser.getGrade() == Grade.BRONZE) {
            User user = userService.findById(loginUser.getId());
            user.setGrade(Grade.SILVER);
            userService.edit(user);
        }

        log.info("글 작성 성공 category : {}", content.getCategory());
        model.addAttribute("msg", "글 작성 성공!");
        model.addAttribute("url", "/content/list/"+content.getCategory());
        return "message";
    }

    @GetMapping("/{content_id}/show")
    public String showContent(Model model, @PathVariable Long content_id,
                              @SessionAttribute(name = "loginUser") User loginUser){
        if(contentRepository.findById(content_id).isEmpty()) {
            model.addAttribute("msg", "존재하지 않는 게시물입니다!");
            model.addAttribute("url", "/");
            return "message";
        }
        if(contentRepository.findById(content_id).get().getCategory() == 3 && loginUser.getGrade().compareTo(Grade.GOLD) < 0) {
            model.addAttribute("msg", "골드 이상만 가능합니다!");
            model.addAttribute("url", "/");
            return "message";
        }

        Content content = contentRepository.findById(content_id).get();
        ContentDTO contentDTO = new ContentDTO(content.getId(), content.getTitle(), content.getUploadDate(),
                content.getTexts(), content.getUserLikes().size(), content.getCategory(), content.getUser().getNickname(),
                content.getImageFiles());
        model.addAttribute("content", contentDTO);

        if(userLikeRepository.findByUserIdAndContentId(loginUser.getId(), content_id).isEmpty()) {
            model.addAttribute("likeCheck", false);

            log.info("likeCheck = {}", false);
        } else {
            model.addAttribute("likeCheck", true);

            log.info("likeCheck = {}", true);
        }

        return "content/show";
    }

    @PostMapping("/{content_id}/show")
    public String likeContent(Model model, @PathVariable Long content_id,
                              @SessionAttribute(name = "loginUser") User loginUser) {
        if(contentRepository.findById(content_id).isEmpty()) {
            model.addAttribute("msg", "존재하지 않는 게시물입니다!");
            model.addAttribute("url", "/");
            return "message";
        }
        if(contentRepository.findById(content_id).get().getCategory() == 3 && loginUser.getGrade().compareTo(Grade.GOLD) < 0) {
            model.addAttribute("msg", "골드 이상만 가능합니다!");
            model.addAttribute("url", "/");
            return "message";
        }

        if(userLikeRepository.findByUserIdAndContentId(loginUser.getId(), content_id).isPresent()) {
            UserLike userLike = userLikeRepository.findByUserIdAndContentId(loginUser.getId(), content_id).get();
            userLike.getUser().getUserLikes().remove(userLike);
            userLike.getContent().getUserLikes().remove(userLike);
            userLike.getContent().getUser().setLikeCount( userLike.getContent().getUser().getLikeCount() - 1 );
            userLike.getContent().setLikeCount( userLike.getContent().getLikeCount() - 1 );
            userLikeRepository.deleteById(userLike.getId());
            Content content = contentRepository.findById(content_id).get();
            ContentDTO contentDTO = new ContentDTO(content.getId(), content.getTitle(), content.getUploadDate(),
                    content.getTexts(), content.getUserLikes().size(), content.getCategory(), content.getUser().getNickname(),
                    content.getImageFiles());
            model.addAttribute("content", contentDTO);
            model.addAttribute("likeCheck", false);
            log.info("좋아요 취소");
            return "/content/show";
        }

        UserLike newUserLike = new UserLike();
        newUserLike.setUser(userService.findById(loginUser.getId()));
        newUserLike.setContent(contentRepository.findById(content_id).get());
        userLikeRepository.save(newUserLike);
        Content content = contentRepository.findById(content_id).get();
        ContentDTO contentDTO = new ContentDTO(content.getId(), content.getTitle(), content.getUploadDate(),
                content.getTexts(), content.getUserLikes().size(), content.getCategory(), content.getUser().getNickname(),
                content.getImageFiles());
        model.addAttribute("content", contentDTO);
        model.addAttribute("likeCheck", true);
        log.info("좋아요");
        return "/content/show";
    }

    @GetMapping("/{content_id}/edit")
    public String editForm(Model model, @PathVariable Long content_id, @SessionAttribute(name = "loginUser") User loginUser){
        if(contentRepository.findById(content_id).isEmpty()) {
            model.addAttribute("msg", "존재하지 않는 게시물입니다!");
            model.addAttribute("url", "/");
            return "message";
        }

        Content content = contentRepository.findById(content_id).get();

        if(!content.getUser().getId().equals(loginUser.getId())) {
            model.addAttribute("msg", "작성자만 수정할 수 있습니다!");
            model.addAttribute("url", "/");
            return "message";
        }

        ContentWriteForm form = new ContentWriteForm();
        form.setCategory(content.getCategory());
        form.setWriter(loginUser.getNickname());
        form.setTitle(content.getTitle());
        form.setTexts(content.getTexts());
        form.setNowImageFiles(content.getImageFiles());
        model.addAttribute("contentWriteForm", form);
        model.addAttribute("contentId", content.getId());
        return "content/editForm";
    }

    @PostMapping("/{content_id}/edit")
    public String edit(@Valid @ModelAttribute ContentWriteForm form, BindingResult bindingResult,
                       @PathVariable Long content_id, Model model) throws IOException {
        if(form.getImageFiles().stream()
                .filter(f -> !f.getContentType().startsWith("image"))
                .findAny()
                .isPresent() && form.getImageFiles().get(0).getSize() != 0) {
            log.info("이미지가 아님!");
            bindingResult.rejectValue("imageFiles", "editFail", "이미지가 아닙니다!");
        }

        if(bindingResult.hasErrors()) {
            log.info("글 수정 실패");
            return "content/editForm";
        }

        uploadFileRepository.deleteAllByContentId(content_id);
        Content content = contentRepository.findById(content_id).get();

        content.setTitle(form.getTitle());
        content.setTexts(form.getTexts());
        List<UploadFile> imageFiles = fileStore.storeFiles(form.getImageFiles(), content);
        content.setImageFiles(imageFiles);
        contentRepository.save(content);

        log.info("글 수정 성공");
        model.addAttribute("msg", "글 수정 성공!");
        model.addAttribute("url", "/content/list/"+content.getCategory());

        return "message";
    }

    @GetMapping("/{content_id}/remove")
    public String remove(@PathVariable Long content_id, Model model) {
        int category = contentRepository.findById(content_id).get().getCategory();
        contentRepository.deleteById(content_id);
        model.addAttribute("msg", "글 삭제 성공!");
        model.addAttribute("url", "/content/list/"+category);

        return "message";
    }

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:"+fileStore.getFullPath(filename));
    }
}
