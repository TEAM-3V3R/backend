package _v3r.project.common.auth.service;

import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.auth.model.CustomUserDetails;
import _v3r.project.common.exception.EverException;
import _v3r.project.user.domain.User;
import _v3r.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) {

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() ->
                        new EverException(ErrorCode.ENTITY_NOT_FOUND)
                );

        return CustomUserDetails.from(user);
    }

}
