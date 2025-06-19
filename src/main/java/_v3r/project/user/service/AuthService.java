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
        User user = userRepository.findByIdName(request.idName())
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        return new LoginUserResponse(user.getId(), user.getIdName(), user.getNickname());
    }

    public void logout(String idName) {
        if (!userRepository.existsByIdName(idName)) {
            throw new EverException(ErrorCode.ENTITY_NOT_FOUND);
        }
    }

}

