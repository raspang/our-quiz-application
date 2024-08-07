package gov.pglds.ourquizapp.repository;

import gov.pglds.ourquizapp.domain.QuizBowlUser;
import gov.pglds.ourquizapp.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the QuizBowlUser entity.
 */
@Repository
public interface QuizBowlUserRepository extends JpaRepository<QuizBowlUser, Long> {
    default Optional<QuizBowlUser> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<QuizBowlUser> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<QuizBowlUser> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select quizBowlUser from QuizBowlUser quizBowlUser left join fetch quizBowlUser.user",
        countQuery = "select count(quizBowlUser) from QuizBowlUser quizBowlUser"
    )
    Page<QuizBowlUser> findAllWithToOneRelationships(Pageable pageable);

    @Query("select quizBowlUser from QuizBowlUser quizBowlUser left join fetch quizBowlUser.user")
    List<QuizBowlUser> findAllWithToOneRelationships();

    @Query("select quizBowlUser from QuizBowlUser quizBowlUser left join fetch quizBowlUser.user where quizBowlUser.id =:id")
    Optional<QuizBowlUser> findOneWithToOneRelationships(@Param("id") Long id);

    QuizBowlUser findByUser(User user);
}
