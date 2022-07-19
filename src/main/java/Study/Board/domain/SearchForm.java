package Study.Board.domain;

import lombok.Data;

@Data
public class SearchForm {

    // 1 : 제목(포함)   2 : 작성자    3 : 좋아요순    4 : 최신순
    private int opt;
    private String str;
}
