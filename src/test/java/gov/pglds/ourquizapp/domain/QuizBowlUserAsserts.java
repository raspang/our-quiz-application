package gov.pglds.ourquizapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class QuizBowlUserAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertQuizBowlUserAllPropertiesEquals(QuizBowlUser expected, QuizBowlUser actual) {
        assertQuizBowlUserAutoGeneratedPropertiesEquals(expected, actual);
        assertQuizBowlUserAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertQuizBowlUserAllUpdatablePropertiesEquals(QuizBowlUser expected, QuizBowlUser actual) {
        assertQuizBowlUserUpdatableFieldsEquals(expected, actual);
        assertQuizBowlUserUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the derived primary key is set correctly.
     *
     * @param entityToPersist the entity used to persist
     * @param persisted the persisted entity
     */
    public static void assertQuizBowlUserMapsIdRelationshipPersistedValue(QuizBowlUser entityToPersist, QuizBowlUser persisted) {
        // Validate the id for MapsId, the ids must be same
        assertThat(entityToPersist.getUser().getId()).isEqualTo(persisted.getId());
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertQuizBowlUserAutoGeneratedPropertiesEquals(QuizBowlUser expected, QuizBowlUser actual) {
        assertThat(expected)
            .as("Verify QuizBowlUser auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertQuizBowlUserUpdatableFieldsEquals(QuizBowlUser expected, QuizBowlUser actual) {
        assertThat(expected)
            .as("Verify QuizBowlUser relevant properties")
            .satisfies(e -> assertThat(e.getScore()).as("check score").isEqualTo(actual.getScore()))
            .satisfies(e -> assertThat(e.getOrganization()).as("check organization").isEqualTo(actual.getOrganization()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertQuizBowlUserUpdatableRelationshipsEquals(QuizBowlUser expected, QuizBowlUser actual) {}
}
