package gov.pglds.ourquizapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class QuizBowlUserTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static QuizBowlUser getQuizBowlUserSample1() {
        return new QuizBowlUser().id(1L).organization("organization1").score(1);
    }

    public static QuizBowlUser getQuizBowlUserSample2() {
        return new QuizBowlUser().id(2L).organization("organization2").score(2);
    }

    public static QuizBowlUser getQuizBowlUserRandomSampleGenerator() {
        return new QuizBowlUser()
            .id(longCount.incrementAndGet())
            .organization(UUID.randomUUID().toString())
            .score(intCount.incrementAndGet());
    }
}
