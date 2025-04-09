package _v3r.project.flask.dto;

public record CategoryFlaskResponse(
        Long promptId, //TODO spring에서 promptId request로 보내면 될까 ?
        String classification,
        String text
) {

}
