package gov.pglds.ourquizapp.web.rest;

import static gov.pglds.ourquizapp.domain.QuizBowlUserAsserts.*;
import static gov.pglds.ourquizapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.pglds.ourquizapp.IntegrationTest;
import gov.pglds.ourquizapp.domain.QuizBowlUser;
import gov.pglds.ourquizapp.domain.User;
import gov.pglds.ourquizapp.repository.QuizBowlUserRepository;
import gov.pglds.ourquizapp.repository.UserRepository;
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
 * Integration tests for the {@link QuizBowlUserResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class QuizBowlUserResourceIT {

    private static final Integer DEFAULT_SCORE = 1;
    private static final Integer UPDATED_SCORE = 2;

    private static final String DEFAULT_ORGANIZATION = "AAAAAAAAAA";
    private static final String UPDATED_ORGANIZATION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/quiz-bowl-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private QuizBowlUserRepository quizBowlUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private QuizBowlUserRepository quizBowlUserRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restQuizBowlUserMockMvc;

    private QuizBowlUser quizBowlUser;

    private QuizBowlUser insertedQuizBowlUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QuizBowlUser createEntity(EntityManager em) {
        QuizBowlUser quizBowlUser = new QuizBowlUser().score(DEFAULT_SCORE).organization(DEFAULT_ORGANIZATION);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        quizBowlUser.setUser(user);
        return quizBowlUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static QuizBowlUser createUpdatedEntity(EntityManager em) {
        QuizBowlUser quizBowlUser = new QuizBowlUser().score(UPDATED_SCORE).organization(UPDATED_ORGANIZATION);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        quizBowlUser.setUser(user);
        return quizBowlUser;
    }

    @BeforeEach
    public void initTest() {
        quizBowlUser = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedQuizBowlUser != null) {
            quizBowlUserRepository.delete(insertedQuizBowlUser);
            insertedQuizBowlUser = null;
        }
    }

    @Test
    @Transactional
    void createQuizBowlUser() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the QuizBowlUser
        var returnedQuizBowlUser = om.readValue(
            restQuizBowlUserMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quizBowlUser)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            QuizBowlUser.class
        );

        // Validate the QuizBowlUser in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertQuizBowlUserUpdatableFieldsEquals(returnedQuizBowlUser, getPersistedQuizBowlUser(returnedQuizBowlUser));

        assertQuizBowlUserMapsIdRelationshipPersistedValue(quizBowlUser, returnedQuizBowlUser);

        insertedQuizBowlUser = returnedQuizBowlUser;
    }

    @Test
    @Transactional
    void createQuizBowlUserWithExistingId() throws Exception {
        // Create the QuizBowlUser with an existing ID
        quizBowlUser.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restQuizBowlUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quizBowlUser)))
            .andExpect(status().isBadRequest());

        // Validate the QuizBowlUser in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void updateQuizBowlUserMapsIdAssociationWithNewId() throws Exception {
        // Initialize the database
        insertedQuizBowlUser = quizBowlUserRepository.saveAndFlush(quizBowlUser);
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Add a new parent entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();

        // Load the quizBowlUser
        QuizBowlUser updatedQuizBowlUser = quizBowlUserRepository.findById(quizBowlUser.getId()).orElseThrow();
        assertThat(updatedQuizBowlUser).isNotNull();
        // Disconnect from session so that the updates on updatedQuizBowlUser are not directly saved in db
        em.detach(updatedQuizBowlUser);

        // Update the User with new association value
        updatedQuizBowlUser.setUser(user);

        // Update the entity
        restQuizBowlUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedQuizBowlUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedQuizBowlUser))
            )
            .andExpect(status().isOk());

        // Validate the QuizBowlUser in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        /**
         * Validate the id for MapsId, the ids must be same
         * Uncomment the following line for assertion. However, please note that there is a known issue and uncommenting will fail the test.
         * Please look at https://github.com/jhipster/generator-jhipster/issues/9100. You can modify this test as necessary.
         * assertThat(testQuizBowlUser.getId()).isEqualTo(testQuizBowlUser.getUser().getId());
         */
    }

    @Test
    @Transactional
    void getAllQuizBowlUsers() throws Exception {
        // Initialize the database
        insertedQuizBowlUser = quizBowlUserRepository.saveAndFlush(quizBowlUser);

        // Get all the quizBowlUserList
        restQuizBowlUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(quizBowlUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].score").value(hasItem(DEFAULT_SCORE)))
            .andExpect(jsonPath("$.[*].organization").value(hasItem(DEFAULT_ORGANIZATION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllQuizBowlUsersWithEagerRelationshipsIsEnabled() throws Exception {
        when(quizBowlUserRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restQuizBowlUserMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(quizBowlUserRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllQuizBowlUsersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(quizBowlUserRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restQuizBowlUserMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(quizBowlUserRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getQuizBowlUser() throws Exception {
        // Initialize the database
        insertedQuizBowlUser = quizBowlUserRepository.saveAndFlush(quizBowlUser);

        // Get the quizBowlUser
        restQuizBowlUserMockMvc
            .perform(get(ENTITY_API_URL_ID, quizBowlUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(quizBowlUser.getId().intValue()))
            .andExpect(jsonPath("$.score").value(DEFAULT_SCORE))
            .andExpect(jsonPath("$.organization").value(DEFAULT_ORGANIZATION));
    }

    @Test
    @Transactional
    void getNonExistingQuizBowlUser() throws Exception {
        // Get the quizBowlUser
        restQuizBowlUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingQuizBowlUser() throws Exception {
        // Initialize the database
        insertedQuizBowlUser = quizBowlUserRepository.saveAndFlush(quizBowlUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the quizBowlUser
        QuizBowlUser updatedQuizBowlUser = quizBowlUserRepository.findById(quizBowlUser.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedQuizBowlUser are not directly saved in db
        em.detach(updatedQuizBowlUser);
        updatedQuizBowlUser.score(UPDATED_SCORE).organization(UPDATED_ORGANIZATION);

        restQuizBowlUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedQuizBowlUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedQuizBowlUser))
            )
            .andExpect(status().isOk());

        // Validate the QuizBowlUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedQuizBowlUserToMatchAllProperties(updatedQuizBowlUser);
    }

    @Test
    @Transactional
    void putNonExistingQuizBowlUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quizBowlUser.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuizBowlUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, quizBowlUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(quizBowlUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuizBowlUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchQuizBowlUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quizBowlUser.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizBowlUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(quizBowlUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuizBowlUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamQuizBowlUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quizBowlUser.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizBowlUserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(quizBowlUser)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the QuizBowlUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateQuizBowlUserWithPatch() throws Exception {
        // Initialize the database
        insertedQuizBowlUser = quizBowlUserRepository.saveAndFlush(quizBowlUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the quizBowlUser using partial update
        QuizBowlUser partialUpdatedQuizBowlUser = new QuizBowlUser();
        partialUpdatedQuizBowlUser.setId(quizBowlUser.getId());

        restQuizBowlUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuizBowlUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuizBowlUser))
            )
            .andExpect(status().isOk());

        // Validate the QuizBowlUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuizBowlUserUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedQuizBowlUser, quizBowlUser),
            getPersistedQuizBowlUser(quizBowlUser)
        );
    }

    @Test
    @Transactional
    void fullUpdateQuizBowlUserWithPatch() throws Exception {
        // Initialize the database
        insertedQuizBowlUser = quizBowlUserRepository.saveAndFlush(quizBowlUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the quizBowlUser using partial update
        QuizBowlUser partialUpdatedQuizBowlUser = new QuizBowlUser();
        partialUpdatedQuizBowlUser.setId(quizBowlUser.getId());

        partialUpdatedQuizBowlUser.score(UPDATED_SCORE).organization(UPDATED_ORGANIZATION);

        restQuizBowlUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQuizBowlUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedQuizBowlUser))
            )
            .andExpect(status().isOk());

        // Validate the QuizBowlUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertQuizBowlUserUpdatableFieldsEquals(partialUpdatedQuizBowlUser, getPersistedQuizBowlUser(partialUpdatedQuizBowlUser));
    }

    @Test
    @Transactional
    void patchNonExistingQuizBowlUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quizBowlUser.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQuizBowlUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, quizBowlUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(quizBowlUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuizBowlUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchQuizBowlUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quizBowlUser.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizBowlUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(quizBowlUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the QuizBowlUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamQuizBowlUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        quizBowlUser.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQuizBowlUserMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(quizBowlUser)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the QuizBowlUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteQuizBowlUser() throws Exception {
        // Initialize the database
        insertedQuizBowlUser = quizBowlUserRepository.saveAndFlush(quizBowlUser);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the quizBowlUser
        restQuizBowlUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, quizBowlUser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return quizBowlUserRepository.count();
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

    protected QuizBowlUser getPersistedQuizBowlUser(QuizBowlUser quizBowlUser) {
        return quizBowlUserRepository.findById(quizBowlUser.getId()).orElseThrow();
    }

    protected void assertPersistedQuizBowlUserToMatchAllProperties(QuizBowlUser expectedQuizBowlUser) {
        assertQuizBowlUserAllPropertiesEquals(expectedQuizBowlUser, getPersistedQuizBowlUser(expectedQuizBowlUser));
    }

    protected void assertPersistedQuizBowlUserToMatchUpdatableProperties(QuizBowlUser expectedQuizBowlUser) {
        assertQuizBowlUserAllUpdatablePropertiesEquals(expectedQuizBowlUser, getPersistedQuizBowlUser(expectedQuizBowlUser));
    }
}
