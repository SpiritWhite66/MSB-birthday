package com.msb.birthday.web.rest;

import com.msb.birthday.BirthdayApp;
import com.msb.birthday.domain.Pattern;
import com.msb.birthday.repository.PatternRepository;
import com.msb.birthday.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static com.msb.birthday.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link PatternResource} REST controller.
 */
@SpringBootTest(classes = BirthdayApp.class)
public class PatternResourceIT {

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    @Autowired
    private PatternRepository patternRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restPatternMockMvc;

    private Pattern pattern;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PatternResource patternResource = new PatternResource(patternRepository);
        this.restPatternMockMvc = MockMvcBuilders.standaloneSetup(patternResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pattern createEntity(EntityManager em) {
        Pattern pattern = new Pattern()
            .message(DEFAULT_MESSAGE);
        return pattern;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pattern createUpdatedEntity(EntityManager em) {
        Pattern pattern = new Pattern()
            .message(UPDATED_MESSAGE);
        return pattern;
    }

    @BeforeEach
    public void initTest() {
        pattern = createEntity(em);
    }

    @Test
    @Transactional
    public void createPattern() throws Exception {
        int databaseSizeBeforeCreate = patternRepository.findAll().size();

        // Create the Pattern
        restPatternMockMvc.perform(post("/api/patterns")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pattern)))
            .andExpect(status().isCreated());

        // Validate the Pattern in the database
        List<Pattern> patternList = patternRepository.findAll();
        assertThat(patternList).hasSize(databaseSizeBeforeCreate + 1);
        Pattern testPattern = patternList.get(patternList.size() - 1);
        assertThat(testPattern.getMessage()).isEqualTo(DEFAULT_MESSAGE);
    }

    @Test
    @Transactional
    public void createPatternWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = patternRepository.findAll().size();

        // Create the Pattern with an existing ID
        pattern.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPatternMockMvc.perform(post("/api/patterns")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pattern)))
            .andExpect(status().isBadRequest());

        // Validate the Pattern in the database
        List<Pattern> patternList = patternRepository.findAll();
        assertThat(patternList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkMessageIsRequired() throws Exception {
        int databaseSizeBeforeTest = patternRepository.findAll().size();
        // set the field null
        pattern.setMessage(null);

        // Create the Pattern, which fails.

        restPatternMockMvc.perform(post("/api/patterns")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pattern)))
            .andExpect(status().isBadRequest());

        List<Pattern> patternList = patternRepository.findAll();
        assertThat(patternList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPatterns() throws Exception {
        // Initialize the database
        patternRepository.saveAndFlush(pattern);

        // Get all the patternList
        restPatternMockMvc.perform(get("/api/patterns?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pattern.getId().intValue())))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)));
    }
    
    @Test
    @Transactional
    public void getPattern() throws Exception {
        // Initialize the database
        patternRepository.saveAndFlush(pattern);

        // Get the pattern
        restPatternMockMvc.perform(get("/api/patterns/{id}", pattern.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(pattern.getId().intValue()))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE));
    }

    @Test
    @Transactional
    public void getNonExistingPattern() throws Exception {
        // Get the pattern
        restPatternMockMvc.perform(get("/api/patterns/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePattern() throws Exception {
        // Initialize the database
        patternRepository.saveAndFlush(pattern);

        int databaseSizeBeforeUpdate = patternRepository.findAll().size();

        // Update the pattern
        Pattern updatedPattern = patternRepository.findById(pattern.getId()).get();
        // Disconnect from session so that the updates on updatedPattern are not directly saved in db
        em.detach(updatedPattern);
        updatedPattern
            .message(UPDATED_MESSAGE);

        restPatternMockMvc.perform(put("/api/patterns")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPattern)))
            .andExpect(status().isOk());

        // Validate the Pattern in the database
        List<Pattern> patternList = patternRepository.findAll();
        assertThat(patternList).hasSize(databaseSizeBeforeUpdate);
        Pattern testPattern = patternList.get(patternList.size() - 1);
        assertThat(testPattern.getMessage()).isEqualTo(UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    public void updateNonExistingPattern() throws Exception {
        int databaseSizeBeforeUpdate = patternRepository.findAll().size();

        // Create the Pattern

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPatternMockMvc.perform(put("/api/patterns")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(pattern)))
            .andExpect(status().isBadRequest());

        // Validate the Pattern in the database
        List<Pattern> patternList = patternRepository.findAll();
        assertThat(patternList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deletePattern() throws Exception {
        // Initialize the database
        patternRepository.saveAndFlush(pattern);

        int databaseSizeBeforeDelete = patternRepository.findAll().size();

        // Delete the pattern
        restPatternMockMvc.perform(delete("/api/patterns/{id}", pattern.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Pattern> patternList = patternRepository.findAll();
        assertThat(patternList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
