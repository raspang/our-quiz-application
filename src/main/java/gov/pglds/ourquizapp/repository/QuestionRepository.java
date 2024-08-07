package gov.pglds.ourquizapp.repository;

import gov.pglds.ourquizapp.domain.Question;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Question entity.
 */
@SuppressWarnings("unused")
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findFirstByEnableTrue();

    @Modifying
    @Query("update Question q set q.enable = false where q.enable = true")
    void disableAllQuestions();
}
