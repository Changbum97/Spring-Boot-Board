package Study.Board.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
public class ContentDTO {
    private Long id;
    private String title;
    private String uploadDate;
    private String texts;
    private Integer likes;
    private int category;
    private String writer;
    private List<UploadFile> imageFiles;
}
