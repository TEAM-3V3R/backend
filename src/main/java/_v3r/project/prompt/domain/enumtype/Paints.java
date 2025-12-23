package _v3r.project.prompt.domain.enumtype;

public enum Paints {

    어해도("fish-paint", PromptTemplate.어해도),
    산수도("mountain-paint", PromptTemplate.산수도),
    탱화("people-paint", PromptTemplate.탱화);

    private final String s3Directory;
    private final PromptTemplate template;

    Paints(String s3Directory, PromptTemplate template) {
        this.s3Directory = s3Directory;
        this.template = template;
    }

    public String getS3Directory() {
        return s3Directory;
    }

    public PromptTemplate getTemplate() {
        return template;
    }
}
