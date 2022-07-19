package Study.Board.domain;

import Study.Board.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {

    private final String fileDir = "D:/개발공부/SpringStudy/Board/files/";

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    public UploadFile storeFile(MultipartFile multipartFile, Content content) throws IOException {
        if(multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFilename = UUID.randomUUID().toString() + "." + extractExt(originalFilename);

        multipartFile.transferTo(new File(getFullPath(storeFilename)));

        UploadFile uploadFile = new UploadFile();
        uploadFile.setUploadFilename(originalFilename);
        uploadFile.setStoreFilename(storeFilename);
        uploadFile.setContent(content);

        return uploadFile;
    }

    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles, Content content) throws IOException {
        List<UploadFile> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if(!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile, content));
            }
        }
        return storeFileResult;
    }

    // 확장자 추출
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}
