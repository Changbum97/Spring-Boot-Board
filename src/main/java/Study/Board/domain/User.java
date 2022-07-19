package Study.Board.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Entity
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String loginId;
    private String password;
    private String nickname;
    private String signupDate;

    private Grade grade;

    private Integer likeCount;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Content> contents = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserLike> userLikes = new ArrayList<>();
}
