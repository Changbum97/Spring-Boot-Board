package Study.Board.domain;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long id;

    private String title;
    private String uploadDate;
    private String texts;

    // 1: 공지사항   2: 자유게시판  3: 골드게시판
    private int category;

    private Integer likeCount;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL)
    private List<UploadFile> imageFiles = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL)
    private List<UserLike> userLikes = new ArrayList<>();

    public void setUser(User user) {
        this.user = user;
        user.getContents().add(this);
    }
}
