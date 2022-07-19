package Study.Board.domain;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class ContentWriteForm {

    @NotEmpty
    private String title;
    private String texts;

    private String writer;
    private int category;

    private List<MultipartFile> imageFiles;
    private List<UploadFile> nowImageFiles;
}
