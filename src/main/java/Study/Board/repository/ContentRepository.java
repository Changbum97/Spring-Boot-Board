package Study.Board.repository;

import Study.Board.domain.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ContentRepository extends JpaRepository<Content, Long> {
    Page<Content> findByCategory(Integer category, Pageable pageable);
    Page<Content> findByCategoryAndUserNickname(Integer category, String userNickname, Pageable pageable);
    Page<Content> findByCategoryAndTitleContains(Integer category, String title, Pageable pageable);
}
