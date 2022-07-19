package Study.Board.domain;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserEditForm {
    private String loginId;
    private String oldNickname;
    @NotEmpty
    private String oldPassword;
    @NotEmpty
    private String password;
    @NotEmpty
    private String passwordCheck;
    @NotEmpty
    private String nickname;
}
