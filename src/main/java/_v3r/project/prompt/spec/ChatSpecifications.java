package _v3r.project.prompt.spec;

import _v3r.project.prompt.domain.Chat;
import _v3r.project.prompt.domain.enumtype.Paints;
import org.springframework.data.jpa.domain.Specification;

public class ChatSpecifications {

    public static Specification<Chat> hasUserId(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Chat> hasPaints(Paints paints) {
        return (root, query, cb) -> cb.equal(root.get("paints"), paints);
    }
}
