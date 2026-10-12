package _v3r.project.prompt.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import _v3r.project.common.s3.S3Service;
import _v3r.project.prompt.domain.Chat;
import _v3r.project.prompt.domain.Prompt;
import _v3r.project.prompt.domain.enumtype.Paints;
import _v3r.project.prompt.dto.response.ImageResponse;
import _v3r.project.prompt.repository.ChatRepository;
import _v3r.project.prompt.repository.PromptRepository;
import _v3r.project.user.domain.User;
import _v3r.project.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;

class PromptServiceTransactionTest {

    private static final String S3_URL = "https://mock-s3/img.png";

    private final AtomicBoolean txOpen = new AtomicBoolean(false);
    private final RestTemplate restTemplate = mock(RestTemplate.class);
    private final S3Service s3Service = mock(S3Service.class);
    private final PromptRepository promptRepository = mock(PromptRepository.class);
    private final ChatRepository chatRepository = mock(ChatRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private PromptService promptService;

    @BeforeEach
    void setUp() {
        // 트랜잭션 열림/닫힘만 추적하는 가짜 트랜잭션 매니저
        PlatformTransactionManager tm = mock(PlatformTransactionManager.class);
        when(tm.getTransaction(any())).thenAnswer(inv -> {
            txOpen.set(true);
            return new SimpleTransactionStatus();
        });
        org.mockito.Mockito.doAnswer(inv -> { txOpen.set(false); return null; }).when(tm).commit(any());
        org.mockito.Mockito.doAnswer(inv -> { txOpen.set(false); return null; }).when(tm).rollback(any());

        promptService = new PromptService(promptRepository, restTemplate, chatRepository, s3Service,
                userRepository, new TransactionTemplate(tm));
        ReflectionTestUtils.setField(promptService, "apiKey", "test");

        User user = User.builder().userId(1L).build();
        Chat chat = Chat.toEntity(user, Paints.어해도);
        when(chatRepository.findById(10L)).thenReturn(Optional.of(chat));
        when(promptRepository.save(any(Prompt.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void 외부_호출_중에는_트랜잭션이_열려_있지_않다() {
        AtomicBoolean txOpenDuringOpenAi = new AtomicBoolean(true);
        AtomicBoolean txOpenDuringS3 = new AtomicBoolean(true);
        when(restTemplate.postForEntity(anyString(), any(), eq(ImageResponse.class))).thenAnswer(inv -> {
            txOpenDuringOpenAi.set(txOpen.get());
            return ResponseEntity.ok(new ImageResponse(null, List.of(new ImageResponse.ImageData("b64"))));
        });
        when(s3Service.uploadImageFromBase64(any(), any(), any(), any(), any())).thenAnswer(inv -> {
            txOpenDuringS3.set(txOpen.get());
            return S3_URL;
        });

        ImageResponse response = promptService.generateImage(1L, 10L, Paints.어해도, "프롬프트");

        assertThat(txOpenDuringOpenAi).isFalse();
        assertThat(txOpenDuringS3).isFalse();
        assertThat(response.data().get(0).base64()).isEqualTo(S3_URL);
        verify(promptRepository).save(any(Prompt.class));
    }

    @Test
    void DB_저장_실패시_업로드한_S3_파일을_삭제한다() {
        when(restTemplate.postForEntity(anyString(), any(), eq(ImageResponse.class)))
                .thenReturn(ResponseEntity.ok(new ImageResponse(null, List.of(new ImageResponse.ImageData("b64")))));
        when(s3Service.uploadImageFromBase64(any(), any(), any(), any(), any())).thenReturn(S3_URL);
        when(promptRepository.save(any(Prompt.class))).thenThrow(new IllegalStateException("db down"));

        assertThatThrownBy(() -> promptService.generateImage(1L, 10L, Paints.어해도, "프롬프트"))
                .isInstanceOf(IllegalStateException.class);

        verify(s3Service).delete(S3_URL);
    }

    @Test
    void 검증_실패시_외부_호출을_하지_않는다() {
        // 다른 사용자의 채팅방
        assertThatThrownBy(() -> promptService.generateImage(2L, 10L, Paints.어해도, "프롬프트"))
                .isInstanceOf(RuntimeException.class);

        verify(restTemplate, never()).postForEntity(anyString(), any(), eq(ImageResponse.class));
        verify(s3Service, never()).delete(any());
    }
}
