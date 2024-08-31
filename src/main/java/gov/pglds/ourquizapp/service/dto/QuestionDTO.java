package gov.pglds.ourquizapp.service.dto;

import gov.pglds.ourquizapp.domain.Question;
import java.io.Serializable;

public class QuestionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //private String question;

    private Integer timer;

    public QuestionDTO() {}

    public QuestionDTO(Question question) {
        this.id = question.getId();
        this.timer = question.getTimer();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTimer() {
        return timer;
    }

    public void setTimer(Integer timer) {
        this.timer = timer;
    }
}
