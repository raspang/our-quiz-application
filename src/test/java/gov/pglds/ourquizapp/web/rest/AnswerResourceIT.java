package gov.pglds.ourquizapp.web.rest;

import static gov.pglds.ourquizapp.domain.AnswerAsserts.*;
import static gov.pglds.ourquizapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.pglds.ourquizapp.IntegrationTest;
import gov.pglds.ourquizapp.domain.Answer;
import gov.pglds.ourquizapp.repository.AnswerRepository;
import gov.pglds.ourquizapp.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AnswerResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AnswerResourceIT {

    private static final String DEFAULT_ANSWER_TEXT = "AAAAAAAAAA";
    private static final String UPDATED_ANSWER_TEXT = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_CORRECT = false;
    private static final Boolean UPDATED_IS_CORRECT = true;

    private static final Boolean DEFAULT_VISIBLE = false;
    private static final Boolean UPDATED_VISIBLE = true;

    private static final String ENTITY_API_URL = "/api/answers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private AnswerRepository answerRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAnswerMockMvc;

    private Answer answer;

    private Answer insertedAnswer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Answer createEntity(EntityManager em) {
        Answer answer = new Answer().answerText(DEFAULT_ANSWER_TEXT).isCorrect(DEFAULT_IS_CORRECT).visible(DEFAULT_VISIBLE);
        return answer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Answer createUpdatedEntity(EntityManager em) {
        Answer answer = new Answer().answerText(UPDATED_ANSWER_TEXT).isCorrect(UPDATED_IS_CORRECT).visible(UPDATED_VISIBLE);
        return answer;
    }

    @BeforeEach
    public void initTest() {
        answer = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedAnswer != null) {
            answerRepository.delete(insertedAnswer);
            insertedAnswer = null;
        }
    }

    @Test
    @Transactional
    void createAnswer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Answer
        var returnedAnswer = om.readValue(
            restAnswerMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(answer)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Answer.class
        );

        // Validate the Answer in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAnswerUpdatableFieldsEquals(returnedAnswer, getPersistedAnswer(returnedAnswer));

        insertedAnswer = returnedAnswer;
    }

    @Test
    @Transactional
    void createAnswerWithExistingId() throws Exception {
        // Create the Answer with an existing ID
        answer.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAnswerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(answer)))
            .andExpect(status().isBadRequest());

        // Validate the Answer in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAnswerTextIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        answer.setAnswerText(null);

        // Create the Answer, which fails.

        restAnswerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(answer)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAnswers() throws Exception {
        // Initialize the database
        insertedAnswer = answerRepository.saveAndFlush(answer);

        // Get all the answerList
        restAnswerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(answer.getId().intValue())))
            .andExpect(jsonPath("$.[*].answerText").value(hasItem(DEFAULT_ANSWER_TEXT)))
            .andExpect(jsonPath("$.[*].isCorrect").value(hasItem(DEFAULT_IS_CORRECT.booleanValue())))
            .andExpect(jsonPath("$.[*].visible").value(hasItem(DEFAULT_VISIBLE.booleanValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAnswersWithEagerRelationshipsIsEnabled() throws Exception {
        when(answerRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAnswerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(answerRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAnswersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(answerRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAnswerMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(answerRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAnswer() throws Exception {
        // Initialize the database
        insertedAnswer = answerRepository.saveAndFlush(answer);

        // Get the answer
        restAnswerMockMvc
            .perform(get(ENTITY_API_URL_ID, answer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(answer.getId().intValue()))
            .andExpect(jsonPath("$.answerText").value(DEFAULT_ANSWER_TEXT))
            .andExpect(jsonPath("$.isCorrect").value(DEFAULT_IS_CORRECT.booleanValue()))
            .andExpect(jsonPath("$.visible").value(DEFAULT_VISIBLE.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingAnswer() throws Exception {
        // Get the answer
        restAnswerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAnswer() throws Exception {
        // Initialize the database
        insertedAnswer = answerRepository.saveAndFlush(answer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the answer
        Answer updatedAnswer = answerRepository.findById(answer.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAnswer are not directly saved in db
        em.detach(updatedAnswer);
        updatedAnswer.answerText(UPDATED_ANSWER_TEXT).isCorrect(UPDATED_IS_CORRECT).visible(UPDATED_VISIBLE);

        restAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAnswer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedAnswer))
            )
            .andExpect(status().isOk());

        // Validate the Answer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAnswerToMatchAllProperties(updatedAnswer);
    }

    @Test
    @Transactional
    void putNonExistingAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        answer.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAnswerMockMvc
            .perform(put(ENTITY_API_URL_ID, answer.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(answer)))
            .andExpect(status().isBadRequest());

        // Validate the Answer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        answer.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnswerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(answer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Answer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        answer.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnswerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(answer)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Answer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAnswerWithPatch() throws Exception {
        // Initialize the database
        insertedAnswer = answerRepository.saveAndFlush(answer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the answer using partial update
        Answer partialUpdatedAnswer = new Answer();
        partialUpdatedAnswer.setId(answer.getId());

        partialUpdatedAnswer.answerText(UPDATED_ANSWER_TEXT);

        restAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAnswer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAnswer))
            )
            .andExpect(status().isOk());

        // Validate the Answer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAnswerUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAnswer, answer), getPersistedAnswer(answer));
    }

    @Test
    @Transactional
    void fullUpdateAnswerWithPatch() throws Exception {
        // Initialize the database
        insertedAnswer = answerRepository.saveAndFlush(answer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the answer using partial update
        Answer partialUpdatedAnswer = new Answer();
        partialUpdatedAnswer.setId(answer.getId());

        partialUpdatedAnswer.answerText(UPDATED_ANSWER_TEXT).isCorrect(UPDATED_IS_CORRECT).visible(UPDATED_VISIBLE);

        restAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAnswer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAnswer))
            )
            .andExpect(status().isOk());

        // Validate the Answer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAnswerUpdatableFieldsEquals(partialUpdatedAnswer, getPersistedAnswer(partialUpdatedAnswer));
    }

    @Test
    @Transactional
    void patchNonExistingAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        answer.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, answer.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(answer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Answer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        answer.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnswerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(answer))
            )
            .andExpect(status().isBadRequest());

        // Validate the Answer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAnswer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        answer.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAnswerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(answer)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Answer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAnswer() throws Exception {
        // Initialize the database
        insertedAnswer = answerRepository.saveAndFlush(answer);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the answer
        restAnswerMockMvc
            .perform(delete(ENTITY_API_URL_ID, answer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return answerRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Answer getPersistedAnswer(Answer answer) {
        return answerRepository.findById(answer.getId()).orElseThrow();
    }

    protected void assertPersistedAnswerToMatchAllProperties(Answer expectedAnswer) {
        assertAnswerAllPropertiesEquals(expectedAnswer, getPersistedAnswer(expectedAnswer));
    }

    protected void assertPersistedAnswerToMatchUpdatableProperties(Answer expectedAnswer) {
        assertAnswerAllUpdatablePropertiesEquals(expectedAnswer, getPersistedAnswer(expectedAnswer));
    }
}
