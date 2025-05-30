package _v3r.project.user.service;

import _v3r.project.common.annotation.AuthUser;
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

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public CreateUserResponse createUser(CreateUserRequest request) {
        User user = request.toEntity();
        userRepository.save(user);
        return CreateUserResponse.of(user);
    }

    public UpdateUserResponse updateUser(User user, UpdateUserRequest request) {

        user.updateUser(request.name());

        userRepository.save(user);

        return UpdateUserResponse.of(user);
    }

    public FindUserResponse findUser(User user,Long userId) {
        return FindUserResponse.of(userId, user.getName(),user.getCreatedAt());
    }


    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

}
