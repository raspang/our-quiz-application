package gov.pglds.ourquizapp.domain;

import static gov.pglds.ourquizapp.domain.QuizBowlUserTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import gov.pglds.ourquizapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class QuizBowlUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(QuizBowlUser.class);
        QuizBowlUser quizBowlUser1 = getQuizBowlUserSample1();
        QuizBowlUser quizBowlUser2 = new QuizBowlUser();
        assertThat(quizBowlUser1).isNotEqualTo(quizBowlUser2);

        quizBowlUser2.setId(quizBowlUser1.getId());
        assertThat(quizBowlUser1).isEqualTo(quizBowlUser2);

        quizBowlUser2 = getQuizBowlUserSample2();
        assertThat(quizBowlUser1).isNotEqualTo(quizBowlUser2);
    }
}
