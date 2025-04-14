package _v3r.project.morpheme.dto.response;

import _v3r.project.morpheme.domain.Morpheme;
import _v3r.project.prompt.domain.Prompt;
import lombok.Builder;

@Builder
public record MorphemeResponse(
        Double josaSum,
        Double nounSum,
        Double verbSum) {

    public Morpheme toEntity(final Prompt prompt) {
        return Morpheme.builder()
                .prompt(prompt)
                .josaSum(this.josaSum)
                .nounSum(this.nounSum)
                .verbSum(this.verbSum)
                .build();
    }

    public static MorphemeResponse of(Double josaSum,Double nounSum, Double verbSum) {
        return MorphemeResponse.builder()
                .josaSum(josaSum)
                .nounSum(nounSum)
                .verbSum(verbSum)
                .build();
    }
}
