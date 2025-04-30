package _v3r.project.user.service;

import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.user.domain.User;
import _v3r.project.user.dto.request.CreateUserRequest;
import _v3r.project.user.dto.response.CreateUserResponse;
import _v3r.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public CreateUserResponse createUser(CreateUserRequest request) {
        User user = request.toEntity();
        userRepository.save(user);
        return CreateUserResponse.of(user);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        userRepository.deleteById(userId);
    }

}
