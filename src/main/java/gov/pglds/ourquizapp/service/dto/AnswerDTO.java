package gov.pglds.ourquizapp.service.dto;

import gov.pglds.ourquizapp.domain.Answer;
import gov.pglds.ourquizapp.domain.Question;
import java.io.Serializable;

public class AnswerDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Boolean visible;

    private Question question;

    public AnswerDTO() {}

    public AnswerDTO(Answer answer) {
        this.id = answer.getId();
        this.visible = true;
        this.question = answer.getQuestion();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
