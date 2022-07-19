package Study.Board.service;

import Study.Board.domain.User;
import Study.Board.domain.UserLike;
import Study.Board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User signup(User user) {
        return userRepository.save(user);
    }

    public User edit(User user) {
        return userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId);
    }

    public Optional<User> findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    public void delete(Long userId) {
        userRepository.delete(userId);
    }

    public List<User> findAll(int page) {
        return userRepository.findAll(page);
    }

    public Long findLastPage() {
        return userRepository.findLastPage();
    }
}