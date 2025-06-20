package _v3r.project.user.service;

import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.user.domain.User;
import _v3r.project.user.dto.request.LoginUserRequest;
import _v3r.project.user.dto.response.LoginUserResponse;
import _v3r.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public LoginUserResponse login(LoginUserRequest request) {
        User user = userRepository.findById(request.id())
                .orElseThrow(() -> new EverException(ErrorCode.DUPLICATE_USER_ID));

        return new LoginUserResponse(user.getUserId(), user.getId(), user.getNickname());
    }

    public void logout(Long userId) {

    }

    public void checkDuplicateId(String id) {
        if (userRepository.existsById(id)) {
            throw new EverException(ErrorCode.DUPLICATE_USER_ID);
        }
    }




}

