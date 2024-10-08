package gov.pglds.ourquizapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Answer.
 */
@Entity
@Table(name = "answer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Answer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "answer_text", nullable = false)
    private String answerText;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "visible")
    private Boolean visible = false;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Answer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnswerText() {
        return this.answerText;
    }

    public Answer answerText(String answerText) {
        this.setAnswerText(answerText);
        return this;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public Boolean getIsCorrect() {
        return this.isCorrect;
    }

    public Answer isCorrect(Boolean isCorrect) {
        this.setIsCorrect(isCorrect);
        return this;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Boolean getVisible() {
        return this.visible;
    }

    public Answer visible(Boolean visible) {
        this.setVisible(visible);
        return this;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Question getQuestion() {
        return this.question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Answer question(Question question) {
        this.setQuestion(question);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Answer user(User user) {
        this.setUser(user);
        return this;
    }

    public void checkAndSetCorrectness() {
        String userAnswered = this.answerText.trim().replaceAll("[^a-zA-Z0-9]", "");

        // Handle null values by assigning an empty string if any of the correct answers are null
        String correctAnswer = this.question.getCorrectAnswer().trim().replaceAll("[^a-zA-Z0-9]", "");
        String correctAnswer2 = this.question.getCorrectAnswer2() != null
            ? this.question.getCorrectAnswer2().trim().replaceAll("[^a-zA-Z0-9]", "")
            : "";
        String correctAnswer3 = this.question.getCorrectAnswer3() != null
            ? this.question.getCorrectAnswer3().trim().replaceAll("[^a-zA-Z0-9]", "")
            : "";
        String correctAnswer4 = this.question.getCorrectAnswer4() != null
            ? this.question.getCorrectAnswer4().trim().replaceAll("[^a-zA-Z0-9]", "")
            : "";

        // Check if user's answer matches any of the correct answers
        this.isCorrect = correctAnswer.equalsIgnoreCase(userAnswered);
        if (!this.isCorrect) {
            this.isCorrect = correctAnswer2.equalsIgnoreCase(userAnswered) ||
            correctAnswer3.equalsIgnoreCase(userAnswered) ||
            correctAnswer4.equalsIgnoreCase(userAnswered);
        }
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Answer)) {
            return false;
        }
        return getId() != null && getId().equals(((Answer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Answer{" +
            "id=" + getId() +
            ", answerText='" + getAnswerText() + "'" +
            ", isCorrect='" + getIsCorrect() + "'" +
            ", visible='" + getVisible() + "'" +
            "}";
    }
}
