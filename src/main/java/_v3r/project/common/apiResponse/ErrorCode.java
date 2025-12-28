package _v3r.project.common.apiResponse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 글로벌 에러
    PARAMETER_INVALID("잘못된 파라미터 입니다.", HttpStatus.BAD_REQUEST),
    METHOD_INVALID("잘못된 METHOD 요청입니다.", HttpStatus.METHOD_NOT_ALLOWED),
    INTERNAL_SERVER_ERROR("서버 내부 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ENTITY_NOT_FOUND("객체를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    ENTITY_TYPE_INVALID("유효하지 않은 엔터티 타입입니다.", HttpStatus.BAD_REQUEST),
    BAD_REQUEST("잘못된 요청입니다", HttpStatus.BAD_REQUEST),
    FORBIDDEN("권한이 없습니다.", HttpStatus.FORBIDDEN),
    //채팅 에러
    ALREADY_FINISHED("채팅이 이미 종료되었습니다.",HttpStatus.FORBIDDEN),
    //s3에러
    JSON_PROCESSING_ERROR("json 직렬화 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_PROCESSING_ERROR("파일처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    S3_DOWNLOAD_FAILED("S3 이미지 다운로드 중 오류 발생",HttpStatus.INTERNAL_SERVER_ERROR),
    // 로그인 에러
    DUPLICATE_USER_ID("이미 존재하는 아이디입니다.", HttpStatus.CONFLICT),
    // auth / jwt 관련 에러
    TOKEN_MISSING("인증 토큰이 없습니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("유효하지 않은 인증 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("인증 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_UNSUPPORTED("지원하지 않는 토큰 형식입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_SIGNATURE_INVALID("토큰 서명이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
    AUTHENTICATION_FAILED("인증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("접근 권한이 없습니다.", HttpStatus.FORBIDDEN);


    private final String message;
    private final HttpStatus httpStatus;

    public int getStatusCode() {
        return httpStatus.value();
    }
}
