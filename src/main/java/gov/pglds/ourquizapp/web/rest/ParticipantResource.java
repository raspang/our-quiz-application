package gov.pglds.ourquizapp.web.rest;

import gov.pglds.ourquizapp.domain.Answer;
import gov.pglds.ourquizapp.domain.Question;
import gov.pglds.ourquizapp.domain.QuizBowlUser;
import gov.pglds.ourquizapp.domain.User;
import gov.pglds.ourquizapp.repository.*;
import gov.pglds.ourquizapp.repository.UserRepository;
import gov.pglds.ourquizapp.security.AuthoritiesConstants;
import gov.pglds.ourquizapp.security.SecurityUtils;
import gov.pglds.ourquizapp.service.ParticipantService;
import gov.pglds.ourquizapp.service.dto.AnswerDTO;
import gov.pglds.ourquizapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link Answer}.
 */
@RestController
@RequestMapping("/api/participants")
@Transactional
public class ParticipantResource {

    private static class AccountResourceException extends RuntimeException {

        private AccountResourceException(String message) {
            super(message);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(ParticipantResource.class);

    private static final String ENTITY_NAME = "participant";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ParticipantRepository participantAnsRepository;

    private final QuestionRepository questionRepository;

    private final UserRepository userRepository;

    private final QuizBowlUserRepository quizBowlUserRepository;

    private final ParticipantService participantService;

    public ParticipantResource(
        ParticipantRepository participantAnsRepository,
        QuestionRepository questionRepository,
        UserRepository userRepository,
        QuizBowlUserRepository quizBowlUserRepository,
        ParticipantService participantService
    ) {
        this.questionRepository = questionRepository;
        this.participantAnsRepository = participantAnsRepository;
        this.userRepository = userRepository;
        this.quizBowlUserRepository = quizBowlUserRepository;
        this.participantService = participantService;
    }

    /**
     * {@code POST  /answers} : Create a new answer.
     *
     * @param answer the answer to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new answer, or with status {@code 400 (Bad Request)} if the answer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.PARTICIPANT + "\")")
    public ResponseEntity<Answer> createAnswer(@Valid @RequestBody Answer answer) throws URISyntaxException {
        log.debug("REST request to save Answer : {}", answer);
        if (answer.getId() != null) {
            throw new BadRequestAlertException("A new answer cannot already have an ID", ENTITY_NAME, "idexists");
        }

        String userLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new ParticipantResource.AccountResourceException("Current user login not found"));

        Optional<User> user = userRepository.findOneByLogin(userLogin);
        User myUser = user.orElseThrow(() -> new ParticipantResource.AccountResourceException("Current user login not found"));

        Optional<QuizBowlUser> quizBowlUser = quizBowlUserRepository.findOneWithToOneRelationships(myUser.getId());
        if (quizBowlUser.isEmpty()) {
            throw new IllegalArgumentException("Please register to the quizBowl table.");
        }

        Optional<Question> question = questionRepository.findFirstByEnableTrue();

        if (question.isEmpty()) {
            throw new IllegalArgumentException("Please wait for the next question.");
        }

        boolean alreadySubmitted = participantAnsRepository.existsByQuestionAndUser(
            question.orElseThrow(() -> new BadRequestAlertException("No enabled question found", ENTITY_NAME, "noenabledquestion")),
            myUser
        );
        if (alreadySubmitted) {
            throw new IllegalArgumentException("You’ve already submitted your answer. Please wait for the next question.");
        }

        answer.setQuestion(
            question.orElseThrow(() -> new BadRequestAlertException("No enabled question found", ENTITY_NAME, "noenabledquestion"))
        );
        answer.setUser(user.orElseThrow(() -> new ParticipantResource.AccountResourceException("Current user login not found")));
        answer.checkAndSetCorrectness();

        answer = participantAnsRepository.save(answer);

        if (answer.getIsCorrect()) {
            QuizBowlUser myQuizBowlUser = quizBowlUser.orElseThrow(
                () -> new ParticipantResource.AccountResourceException("Current QuizBowlUser is not found in the list")
            );
            myQuizBowlUser.setScore(myQuizBowlUser.getScore() + answer.getQuestion().getDifficultyLevel());
            quizBowlUserRepository.save(myQuizBowlUser);
        }

        return ResponseEntity.created(new URI("/api/participants/" + answer.getId()))
            .headers(createEntityCreationAlert2(applicationName, answer.getAnswerText(), answer.getQuestion().getNumber()))
            .body(answer);
    }

    private static HttpHeaders createEntityCreationAlert2(String applicationName, String param1, Integer param2) {
        String message = "Your answer has been successfully submitted.";
        return createAlert2(applicationName, message, param1);
    }

    private static HttpHeaders createAlert2(String applicationName, String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-" + applicationName + "-alert", message);
        //        try {
        //            headers.add("X-" + applicationName + "-params", URLEncoder.encode(param, StandardCharsets.UTF_8.toString()));
        //        } catch (UnsupportedEncodingException e) {
        //            // StandardCharsets are supported by every Java implementation so this exception will never happen
        //        }
        return headers;
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
    //    @PutMapping("/{id}")
    //    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.PARTICIPANT + "\")")
    //    public ResponseEntity<Answer> updateAnswer(
    //        @PathVariable(value = "id", required = false) final Long id,
    //        @Valid @RequestBody Answer answer
    //    ) throws URISyntaxException {
    //        log.debug("REST request to update Answer : {}, {}", id, answer);
    //        if (answer.getId() == null) {
    //            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    //        }
    //        if (!Objects.equals(id, answer.getId())) {
    //            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    //        }
    //
    //        if (!answerRepository.existsById(id)) {
    //            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    //        }
    //
    //
    //        answer = participantAnsRepository.save(answer);
    //        return ResponseEntity.ok()
    //            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, answer.getId().toString()))
    //            .body(answer);
    //    }

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
    //    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    //    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.PARTICIPANT + "\")")
    //    public ResponseEntity<Answer> partialUpdateAnswer(
    //        @PathVariable(value = "id", required = false) final Long id,
    //        @NotNull @RequestBody Answer answer
    //    ) throws URISyntaxException {
    //        log.debug("REST request to partial update Answer partially : {}, {}", id, answer);
    //        if (answer.getId() == null) {
    //            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
    //        }
    //        if (!Objects.equals(id, answer.getId())) {
    //            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
    //        }
    //
    //        if (!participantAnsRepository.existsById(id)) {
    //            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
    //        }
    //
    //        Optional<Answer> result = participantAnsRepository
    //            .findById(answer.getId())
    //            .map(existingAnswer -> {
    //                if (answer.getAnswerText() != null) {
    //                    existingAnswer.setAnswerText(answer.getAnswerText());
    //                }
    //                if (answer.getIsCorrect() != null) {
    //                    existingAnswer.setIsCorrect(answer.getIsCorrect());
    //                }
    //
    //                return existingAnswer;
    //            })
    //            .map(participantAnsRepository::save);
    //
    //        return ResponseUtil.wrapOrNotFound(
    //            result,
    //            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, answer.getId().toString())
    //        );
    //    }

    /**
     * {@code GET  /answers} : get all the answers.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of answers in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.PARTICIPANT + "\")")
    public ResponseEntity<List<Answer>> getAllAnswers(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of Answers");
        Page<Answer> page;

        int pageSize = Integer.MAX_VALUE; // or a specific large number
        pageable = PageRequest.of(0, pageSize, Sort.by("id").ascending());
        page = participantAnsRepository.findAllWithEagerRelationships(pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/taken")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.PARTICIPANT + "\")")
    public ResponseEntity<List<AnswerDTO>> getAllTakens() {
        log.debug("REST request to get a page of Answers");

        List<AnswerDTO> list = participantService.getQuestionsWithAnswerStatus(); //participantAnsRepository.findAllWithToOneRelationships();

        // HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().body(list);
    }

    /**
     * {@code GET  /answers/:id} : get the "id" answer.
     *
     * @param id the id of the answer to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the answer, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.PARTICIPANT + "\")")
    public ResponseEntity<Answer> getAnswer(@PathVariable("id") Long id) {
        log.debug("REST request to get Answer : {}", id);
        Optional<Answer> answer = participantAnsRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(answer);
    }
    /**
     * {@code DELETE  /answers/:id} : delete the "id" answer.
     *
     * @param id the id of the answer to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    //    @DeleteMapping("/{id}")
    //    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.PARTICIPANT + "\")")
    //    public ResponseEntity<Void> deleteAnswer(@PathVariable("id") Long id) {
    //        log.debug("REST request to delete Answer : {}", id);
    //        answerRepository.deleteById(id);
    //        return ResponseEntity.noContent()
    //            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
    //            .build();
    //    }
}
