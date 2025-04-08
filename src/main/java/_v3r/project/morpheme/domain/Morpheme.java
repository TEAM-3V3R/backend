package _v3r.project.morpheme.domain;

import _v3r.project.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "category")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Morpheme extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "morpheme_id")
    private Long id;

    //TODO 카테고리랑만 조인할지, prompt와도 조인할지 검토

    @Column(name = "josa_sum")
    private Long josaSum;

    @Column(name = "noun_sum")
    private Long nounSum;

    @Column(name = "verb_sum")
    private Long verbSum;

    @Column(name = "punctuation_sum")
    private Long punctuationSum;
}
