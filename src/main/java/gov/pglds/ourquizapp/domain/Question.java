package gov.pglds.ourquizapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Question.
 */
@Entity
@Table(name = "question")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "number", nullable = false)
    private Integer number;

    @NotNull
    @Column(name = "question_text", nullable = false, length = 500)
    private String questionText;

    @Column(name = "difficulty_level")
    private Integer difficultyLevel;

    @NotNull
    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;

    @Column(name = "enable")
    private Boolean enable;

    @Column(name = "correct_answer_2")
    private String correctAnswer2;

    @Column(name = "correct_answer_3")
    private String correctAnswer3;

    @Column(name = "correct_answer_4")
    private String correctAnswer4;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Question id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumber() {
        return this.number;
    }

    public Question number(Integer number) {
        this.setNumber(number);
        return this;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getQuestionText() {
        return this.questionText;
    }

    public Question questionText(String questionText) {
        this.setQuestionText(questionText);
        return this;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Integer getDifficultyLevel() {
        return this.difficultyLevel;
    }

    public Question difficultyLevel(Integer difficultyLevel) {
        this.setDifficultyLevel(difficultyLevel);
        return this;
    }

    public void setDifficultyLevel(Integer difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getCorrectAnswer() {
        return this.correctAnswer;
    }

    public Question correctAnswer(String correctAnswer) {
        this.setCorrectAnswer(correctAnswer);
        return this;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Boolean getEnable() {
        return this.enable;
    }

    public Question enable(Boolean enable) {
        this.setEnable(enable);
        return this;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getCorrectAnswer2() {
        return this.correctAnswer2;
    }

    public Question correctAnswer2(String correctAnswer2) {
        this.setCorrectAnswer2(correctAnswer2);
        return this;
    }

    public void setCorrectAnswer2(String correctAnswer2) {
        this.correctAnswer2 = correctAnswer2;
    }

    public String getCorrectAnswer3() {
        return this.correctAnswer3;
    }

    public Question correctAnswer3(String correctAnswer3) {
        this.setCorrectAnswer3(correctAnswer3);
        return this;
    }

    public void setCorrectAnswer3(String correctAnswer3) {
        this.correctAnswer3 = correctAnswer3;
    }

    public String getCorrectAnswer4() {
        return this.correctAnswer4;
    }

    public Question correctAnswer4(String correctAnswer4) {
        this.setCorrectAnswer4(correctAnswer4);
        return this;
    }

    public void setCorrectAnswer4(String correctAnswer4) {
        this.correctAnswer4 = correctAnswer4;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Question)) {
            return false;
        }
        return getId() != null && getId().equals(((Question) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Question{" +
            "id=" + getId() +
            ", number=" + getNumber() +
            ", questionText='" + getQuestionText() + "'" +
            ", difficultyLevel=" + getDifficultyLevel() +
            ", correctAnswer='" + getCorrectAnswer() + "'" +
            ", enable='" + getEnable() + "'" +
            ", correctAnswer2='" + getCorrectAnswer2() + "'" +
            ", correctAnswer3='" + getCorrectAnswer3() + "'" +
            ", correctAnswer4='" + getCorrectAnswer4() + "'" +
            "}";
    }
}
