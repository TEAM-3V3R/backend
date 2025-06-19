package _v3r.project.user.service;

import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.user.domain.User;
import _v3r.project.user.dto.request.CreateUserRequest;
import _v3r.project.user.dto.request.UpdateUserRequest;
import _v3r.project.user.dto.response.CreateUserResponse;
import _v3r.project.user.dto.response.FindUserResponse;
import _v3r.project.user.dto.response.UpdateUserResponse;
import _v3r.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsById(request.toEntity().getId())) {
            throw new EverException(ErrorCode.DUPLICATE_USER_ID);
        }
        User user = request.toEntity();
        userRepository.save(user);
        return CreateUserResponse.of(user);
    }
    @Transactional
    public UpdateUserResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        user.updateUser(request.nickName());

        userRepository.save(user);

        return UpdateUserResponse.of(user);
    }
    @Transactional(readOnly = true)
    public FindUserResponse findUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        return FindUserResponse.of(user.getUserId(), user.getId(),user.getNickname(),user.getCreatedAt());
    }
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        userRepository.deleteById(userId);
    }

}
