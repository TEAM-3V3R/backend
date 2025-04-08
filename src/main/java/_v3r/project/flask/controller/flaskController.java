package _v3r.project.flask.controller;

import _v3r.project.common.apiResponse.CustomApiResponse;
import _v3r.project.flask.dto.FlaskRequest;
import _v3r.project.flask.dto.FlaskResponse;
import _v3r.project.flask.service.FlaskService;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class flaskController {
    //TODO flask로 사용자 프롬프트 전송(test로는 message로 request)
    //TODO flask -> spring으로 결과 전송 -> db저장
    private final FlaskService flaskService;

    @PostMapping("/test")
    public CustomApiResponse<FlaskResponse> postAbstractive(@RequestBody FlaskRequest flaskRequest) {
        FlaskResponse response = flaskService.getAbstractive(flaskRequest);
        System.out.println("요청 도착: " + response);
        return CustomApiResponse.success(response, 200, "요청 성공");
    }

    @PostMapping("/")
    public ResponseEntity<FlaskResponse> getAbstractive(@RequestBody FlaskRequest flaskRequest) {
        System.out.println("플라스크 요청 받음: " + flaskRequest);
        FlaskResponse dummyResponse = new FlaskResponse(HttpStatus.OK, "응답 성공");
        return new ResponseEntity<>(dummyResponse, HttpStatus.OK);
    }

}
