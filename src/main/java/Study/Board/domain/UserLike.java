package Study.Board.domain;

import lombok.Data;

import javax.persistence.*;


@Data
@Entity
public class UserLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    public void setUser(User user) {
        this.user = user;
        user.getUserLikes().add(this);
    }
    public void setContent(Content content) {
        this.content = content;
        content.getUserLikes().add(this);
        content.getUser().setLikeCount( content.getUser().getLikeCount() + 1 );
        content.setLikeCount( content.getLikeCount() + 1);
        if(content.getUser().getLikeCount() >= 10) {
            content.getUser().setRole("GOLD");
        }
    }
}
