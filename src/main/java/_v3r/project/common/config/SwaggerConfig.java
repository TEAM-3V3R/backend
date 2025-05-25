package _v3r.project.common.config;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "3V3R API 명세서",
                description = "백엔드 API")
)
@Configuration
public class SwaggerConfig {

}