package gov.pglds.ourquizapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class QuestionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Question getQuestionSample1() {
        return new Question().id(1L).number(1).difficultyLevel(1).questionText("questionText1").correctAnswer("correctAnswer1");
    }

    public static Question getQuestionSample2() {
        return new Question().id(2L).number(2).difficultyLevel(2).questionText("questionText2").correctAnswer("correctAnswer2");
    }

    public static Question getQuestionRandomSampleGenerator() {
        return new Question()
            .id(longCount.incrementAndGet())
            .number(intCount.incrementAndGet())
            .difficultyLevel(intCount.incrementAndGet())
            .questionText(UUID.randomUUID().toString())
            .correctAnswer(UUID.randomUUID().toString());
    }
}
