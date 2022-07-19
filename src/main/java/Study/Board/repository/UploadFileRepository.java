package Study.Board.repository;

import Study.Board.domain.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {
    void deleteAllByContentId(Long contentId);
}