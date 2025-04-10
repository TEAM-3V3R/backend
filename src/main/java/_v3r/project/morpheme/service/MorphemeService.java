package _v3r.project.morpheme.service;

import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.common.apiResponse.ErrorCode;
import _v3r.project.common.exception.EverException;
import _v3r.project.flask.service.FlaskService;
import _v3r.project.morpheme.domain.Morpheme;
import _v3r.project.morpheme.dto.response.MorphemeResponse;
import _v3r.project.morpheme.repository.MorphemeRepository;
import _v3r.project.prompt.domain.Prompt;
import _v3r.project.prompt.repository.PromptRepository;
import _v3r.project.user.domain.User;
import _v3r.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MorphemeService {
    private final FlaskService flaskService;
    private final PromptRepository promptRepository;
    private final UserRepository userRepository;
    private final MorphemeRepository morphemeRepository;

    @Transactional
    public MorphemeResponse receiveMorpheme(Long userId,Long promptId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new EverException(ErrorCode.ENTITY_NOT_FOUND));

        CustomApiResponse<MorphemeResponse> flaskResponse = flaskService.receiveMorpheme(promptId);
        MorphemeResponse morphemeData = flaskResponse.data();
        MorphemeResponse response = MorphemeResponse.of(morphemeData.josaSum(),
                morphemeData.nounSum(), morphemeData.verbSum());

        Morpheme morpheme = response.toEntity(prompt);

        morphemeRepository.save(morpheme);

        return response;
    }

}
