package _v3r.project.flask.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FlaskResponse(@JsonProperty("status") Long statusCode, String message) {

}
