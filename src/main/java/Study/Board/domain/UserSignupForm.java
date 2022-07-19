package Study.Board.domain;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserSignupForm {
    @NotEmpty
    private String loginId;
    @NotEmpty
    private String password;
    @NotEmpty
    private String passwordCheck;
    @NotEmpty
    private String nickname;
}
