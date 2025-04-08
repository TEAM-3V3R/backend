package _v3r.project.flask.dto;

import org.springframework.http.HttpStatus;
//TODO Test response
public record FlaskResponse(HttpStatus httpStatus,String message) {

}
