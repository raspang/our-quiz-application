package gov.pglds.ourquizapp.web.rest;

import gov.pglds.ourquizapp.domain.QuizBowlUser;
import gov.pglds.ourquizapp.repository.QuizBowlUserRepository;
import gov.pglds.ourquizapp.repository.UserRepository;
import gov.pglds.ourquizapp.security.AuthoritiesConstants;
import gov.pglds.ourquizapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link gov.pglds.ourquizapp.domain.QuizBowlUser}.
 */
@RestController
@RequestMapping("/api/quiz-bowl-users")
@Transactional
public class QuizBowlUserResource {

    private static final Logger log = LoggerFactory.getLogger(QuizBowlUserResource.class);

    private static final String ENTITY_NAME = "quizBowlUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final QuizBowlUserRepository quizBowlUserRepository;

    private final UserRepository userRepository;

    public QuizBowlUserResource(QuizBowlUserRepository quizBowlUserRepository, UserRepository userRepository) {
        this.quizBowlUserRepository = quizBowlUserRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@code POST  /quiz-bowl-users} : Create a new quizBowlUser.
     *
     * @param quizBowlUser the quizBowlUser to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new quizBowlUser, or with status {@code 400 (Bad Request)} if the quizBowlUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<QuizBowlUser> createQuizBowlUser(@RequestBody QuizBowlUser quizBowlUser) throws URISyntaxException {
        log.debug("REST request to save QuizBowlUser : {}", quizBowlUser);
        if (quizBowlUser.getId() != null) {
            throw new BadRequestAlertException("A new quizBowlUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (Objects.isNull(quizBowlUser.getUser())) {
            throw new BadRequestAlertException("Invalid association value provided", ENTITY_NAME, "null");
        }
        Long userId = quizBowlUser.getUser().getId();
        userRepository.findById(userId).ifPresent(quizBowlUser::user);
        quizBowlUser = quizBowlUserRepository.save(quizBowlUser);
        return ResponseEntity.created(new URI("/api/quiz-bowl-users/" + quizBowlUser.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, quizBowlUser.getId().toString()))
            .body(quizBowlUser);
    }

    /**
     * {@code PUT  /quiz-bowl-users/:id} : Updates an existing quizBowlUser.
     *
     * @param id the id of the quizBowlUser to save.
     * @param quizBowlUser the quizBowlUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated quizBowlUser,
     * or with status {@code 400 (Bad Request)} if the quizBowlUser is not valid,
     * or with status {@code 500 (Internal Server Error)} if the quizBowlUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<QuizBowlUser> updateQuizBowlUser(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody QuizBowlUser quizBowlUser
    ) throws URISyntaxException {
        log.debug("REST request to update QuizBowlUser : {}, {}", id, quizBowlUser);
        if (quizBowlUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, quizBowlUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!quizBowlUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        quizBowlUser = quizBowlUserRepository.save(quizBowlUser);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, quizBowlUser.getId().toString()))
            .body(quizBowlUser);
    }

    /**
     * {@code PATCH  /quiz-bowl-users/:id} : Partial updates given fields of an existing quizBowlUser, field will ignore if it is null
     *
     * @param id the id of the quizBowlUser to save.
     * @param quizBowlUser the quizBowlUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated quizBowlUser,
     * or with status {@code 400 (Bad Request)} if the quizBowlUser is not valid,
     * or with status {@code 404 (Not Found)} if the quizBowlUser is not found,
     * or with status {@code 500 (Internal Server Error)} if the quizBowlUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<QuizBowlUser> partialUpdateQuizBowlUser(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody QuizBowlUser quizBowlUser
    ) throws URISyntaxException {
        log.debug("REST request to partial update QuizBowlUser partially : {}, {}", id, quizBowlUser);
        if (quizBowlUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, quizBowlUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!quizBowlUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<QuizBowlUser> result = quizBowlUserRepository
            .findById(quizBowlUser.getId())
            .map(existingQuizBowlUser -> {
                if (quizBowlUser.getOrganization() != null) {
                    existingQuizBowlUser.setOrganization(quizBowlUser.getOrganization());
                }
                if (quizBowlUser.getScore() != null) {
                    existingQuizBowlUser.setScore(quizBowlUser.getScore());
                }

                return existingQuizBowlUser;
            })
            .map(quizBowlUserRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, quizBowlUser.getId().toString())
        );
    }

    /**
     * {@code GET  /quiz-bowl-users} : get all the quizBowlUsers.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of quizBowlUsers in body.
     */
    @GetMapping("")
    @Transactional(readOnly = true)
    public List<QuizBowlUser> getAllQuizBowlUsers(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get all QuizBowlUsers");
        if (eagerload) {
            return quizBowlUserRepository.findAllWithEagerRelationships();
        } else {
            return quizBowlUserRepository.findAll();
        }
    }

    /**
     * {@code GET  /quiz-bowl-users/:id} : get the "id" quizBowlUser.
     *
     * @param id the id of the quizBowlUser to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the quizBowlUser, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<QuizBowlUser> getQuizBowlUser(@PathVariable("id") Long id) {
        log.debug("REST request to get QuizBowlUser : {}", id);
        Optional<QuizBowlUser> quizBowlUser = quizBowlUserRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(quizBowlUser);
    }

    /**
     * {@code DELETE  /quiz-bowl-users/:id} : delete the "id" quizBowlUser.
     *
     * @param id the id of the quizBowlUser to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Void> deleteQuizBowlUser(@PathVariable("id") Long id) {
        log.debug("REST request to delete QuizBowlUser : {}", id);
        quizBowlUserRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
