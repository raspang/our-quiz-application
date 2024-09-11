package gov.pglds.ourquizapp.web.rest;

import gov.pglds.ourquizapp.domain.Answer;
import gov.pglds.ourquizapp.domain.Question;
import gov.pglds.ourquizapp.domain.QuizBowlUser;
import gov.pglds.ourquizapp.repository.AnswerRepository;
import gov.pglds.ourquizapp.repository.QuizBowlUserRepository;
import gov.pglds.ourquizapp.security.AuthoritiesConstants;
import gov.pglds.ourquizapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link gov.pglds.ourquizapp.domain.Answer}.
 */
@RestController
@RequestMapping("/api/answers")
@Transactional
public class AnswerResource {

    private static final Logger log = LoggerFactory.getLogger(AnswerResource.class);

    private static final String ENTITY_NAME = "answer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AnswerRepository answerRepository;

    private final QuizBowlUserRepository quizBowlUserRepository;

    public AnswerResource(AnswerRepository answerRepository, QuizBowlUserRepository quizBowlUserRepository) {
        this.answerRepository = answerRepository;
        this.quizBowlUserRepository = quizBowlUserRepository;
    }

    /**
     * {@code POST  /answers} : Create a new answer.
     *
     * @param answer the answer to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new answer, or with status {@code 400 (Bad Request)} if the answer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Answer> createAnswer(@Valid @RequestBody Answer answer) throws URISyntaxException {
        log.debug("REST request to save Answer : {}", answer);
        if (answer.getId() != null) {
            throw new BadRequestAlertException("A new answer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        answer = answerRepository.save(answer);
        return ResponseEntity.created(new URI("/api/answers/" + answer.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, answer.getId().toString()))
            .body(answer);
    }

    @PutMapping("/enable")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Void> enableVisible() {
        answerRepository.visibleAllAnswers();
        return ResponseEntity.ok().build();
    }

    /**
     * {@code PUT  /answers/:id} : Updates an existing answer.
     *
     * @param id the id of the answer to save.
     * @param answer the answer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated answer,
     * or with status {@code 400 (Bad Request)} if the answer is not valid,
     * or with status {@code 500 (Internal Server Error)} if the answer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Answer> updateAnswer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Answer answer
    ) throws URISyntaxException {
        log.debug("REST request to update Answer : {}, {}", id, answer);
        if (answer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, answer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!answerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        // update the score in quizBowl
        Optional<Answer> preAnsOps = answerRepository.findById(id);
        Answer preAns = preAnsOps.orElseThrow(() -> new BadRequestAlertException("Error: no Answer found", ENTITY_NAME, "Answer"));

        Optional<QuizBowlUser> quizBowlUser = quizBowlUserRepository.findOneWithToOneRelationships(answer.getUser().getId());

        QuizBowlUser myQuizBowlUser = getQuizBowlUser(answer, quizBowlUser, preAns);
        quizBowlUserRepository.save(myQuizBowlUser);

        answer = answerRepository.save(answer);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, answer.getId().toString()))
            .body(answer);
    }

    private static QuizBowlUser getQuizBowlUser(Answer answer, Optional<QuizBowlUser> quizBowlUser, Answer preAns) {
        QuizBowlUser myQuizBowlUser = quizBowlUser.orElseThrow(() -> new BadRequestAlertException("Error", ENTITY_NAME, "quizBowlUser"));

        boolean isCorrect = preAns.getIsCorrect();
        if (!isCorrect && answer.getIsCorrect()) {
            myQuizBowlUser.setScore(myQuizBowlUser.getScore() + answer.getQuestion().getDifficultyLevel());
        } else if (isCorrect && !answer.getIsCorrect()) {
            myQuizBowlUser.setScore(myQuizBowlUser.getScore() - answer.getQuestion().getDifficultyLevel());
        }

        return myQuizBowlUser;
    }

    /**
     * {@code PATCH  /answers/:id} : Partial updates given fields of an existing answer, field will ignore if it is null
     *
     * @param id the id of the answer to save.
     * @param answer the answer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated answer,
     * or with status {@code 400 (Bad Request)} if the answer is not valid,
     * or with status {@code 404 (Not Found)} if the answer is not found,
     * or with status {@code 500 (Internal Server Error)} if the answer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Answer> partialUpdateAnswer(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Answer answer
    ) throws URISyntaxException {
        log.debug("REST request to partial update Answer partially : {}, {}", id, answer);
        if (answer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, answer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!answerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Answer> result = answerRepository
            .findById(answer.getId())
            .map(existingAnswer -> {
                if (answer.getAnswerText() != null) {
                    existingAnswer.setAnswerText(answer.getAnswerText());
                }
                if (answer.getIsCorrect() != null) {
                    existingAnswer.setIsCorrect(answer.getIsCorrect());
                }
                if (answer.getVisible() != null) {
                    existingAnswer.setVisible(answer.getVisible());
                }

                return existingAnswer;
            })
            .map(answerRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, answer.getId().toString())
        );
    }

    /**
     * {@code GET  /answers} : get all the answers.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of answers in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<List<Answer>> getAllAnswers(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of Answers");
        Page<Answer> page;
        if (eagerload) {
            page = answerRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = answerRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /answers/:id} : get the "id" answer.
     *
     * @param id the id of the answer to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the answer, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Answer> getAnswer(@PathVariable("id") Long id) {
        log.debug("REST request to get Answer : {}", id);
        Optional<Answer> answer = answerRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(answer);
    }

    /**
     * {@code DELETE  /answers/:id} : delete the "id" answer.
     *
     * @param id the id of the answer to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Void> deleteAnswer(@PathVariable("id") Long id) {
        log.debug("REST request to delete Answer : {}", id);
        answerRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
