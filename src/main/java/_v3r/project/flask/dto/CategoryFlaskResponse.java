package _v3r.project.flask.dto;

public record CategoryFlaskResponse(
        //TODO FlaskResponse로 공통응답 설정해놓고 해당 response 삭제하고 통합해야할듯
        Long promptId,
        String classification,
        String text
) {

}
