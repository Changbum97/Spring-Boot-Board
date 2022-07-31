package Study.Board.repository;

import Study.Board.domain.User;
import Study.Board.domain.UserLike;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional
public class UserRepository {
    private final EntityManager em;

    public User save(User user) {
        em.persist(user);
        return findById(user.getId());
    }
    public User findById(Long id) {
        return em.find(User.class, id);
    }

    public Optional<User> findByLoginId(String loginId) {
        return em.createQuery("select u from User u where u.loginId=:loginId")
                .setParameter("loginId", loginId)
                .getResultStream()
                .findAny();
    }

    public Optional<User> findByNickname(String nickname) {
        return em.createQuery("select u from User u where u.nickname=:nickname")
                .setParameter("nickname", nickname)
                .getResultStream()
                .findAny();
    }

    public void delete(Long userId) {
        em.remove(findById(userId));
        return;
    }

    public List<User> findAll(int page) {
        return em.createQuery("select u from User u")
                .setFirstResult(page * 10)
                .setMaxResults(10)
                .getResultList();
    }

    public Long findLastPage() {
        return (em.createQuery("select u from User u")
                .getResultStream().count() - 1) / 10 + 1;
    }

}
