package _v3r.project.prompt.domain.enumtype;

public enum Paints {
    어해도(PromptTemplate.어해도),
    산수도(PromptTemplate.산수도),
    탱화(PromptTemplate.탱화);
    private final PromptTemplate template;
    Paints(PromptTemplate template) {
        this.template = template;
    }

    public PromptTemplate getTemplate() {
        return template;
    }
}
