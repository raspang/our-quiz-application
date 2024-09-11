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
        return new Question()
            .id(1L)
            .number(1)
            .questionText("questionText1")
            .difficultyLevel(1)
            .correctAnswer("correctAnswer1")
            .correctAnswer2("correctAnswer21")
            .correctAnswer3("correctAnswer31")
            .correctAnswer4("correctAnswer41");
    }

    public static Question getQuestionSample2() {
        return new Question()
            .id(2L)
            .number(2)
            .questionText("questionText2")
            .difficultyLevel(2)
            .correctAnswer("correctAnswer2")
            .correctAnswer2("correctAnswer22")
            .correctAnswer3("correctAnswer32")
            .correctAnswer4("correctAnswer42");
    }

    public static Question getQuestionRandomSampleGenerator() {
        return new Question()
            .id(longCount.incrementAndGet())
            .number(intCount.incrementAndGet())
            .questionText(UUID.randomUUID().toString())
            .difficultyLevel(intCount.incrementAndGet())
            .correctAnswer(UUID.randomUUID().toString())
            .correctAnswer2(UUID.randomUUID().toString())
            .correctAnswer3(UUID.randomUUID().toString())
            .correctAnswer4(UUID.randomUUID().toString());
    }
}
