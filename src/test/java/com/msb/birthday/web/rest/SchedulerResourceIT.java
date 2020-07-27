package com.msb.birthday.web.rest;

import com.msb.birthday.BirthdayApp;
import com.msb.birthday.domain.Scheduler;
import com.msb.birthday.repository.SchedulerRepository;
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
 * Integration tests for the {@link SchedulerResource} REST controller.
 */
@SpringBootTest(classes = BirthdayApp.class)
public class SchedulerResourceIT {

    private static final String DEFAULT_ID_GUILD_SERVER = "AAAAAAAAAA";
    private static final String UPDATED_ID_GUILD_SERVER = "BBBBBBBBBB";

    private static final String DEFAULT_ID_CHANNEL = "AAAAAAAAAA";
    private static final String UPDATED_ID_CHANNEL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVATED = false;
    private static final Boolean UPDATED_ACTIVATED = true;

    private static final Long DEFAULT_HOUR = 1L;
    private static final Long UPDATED_HOUR = 2L;

    @Autowired
    private SchedulerRepository schedulerRepository;

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

    private MockMvc restSchedulerMockMvc;

    private Scheduler scheduler;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SchedulerResource schedulerResource = new SchedulerResource(schedulerRepository);
        this.restSchedulerMockMvc = MockMvcBuilders.standaloneSetup(schedulerResource)
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
    public static Scheduler createEntity(EntityManager em) {
        Scheduler scheduler = new Scheduler()
            .idGuildServer(DEFAULT_ID_GUILD_SERVER)
            .idChannel(DEFAULT_ID_CHANNEL)
            .activated(DEFAULT_ACTIVATED)
            .hour(DEFAULT_HOUR);
        return scheduler;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Scheduler createUpdatedEntity(EntityManager em) {
        Scheduler scheduler = new Scheduler()
            .idGuildServer(UPDATED_ID_GUILD_SERVER)
            .idChannel(UPDATED_ID_CHANNEL)
            .activated(UPDATED_ACTIVATED)
            .hour(UPDATED_HOUR);
        return scheduler;
    }

    @BeforeEach
    public void initTest() {
        scheduler = createEntity(em);
    }

    @Test
    @Transactional
    public void createScheduler() throws Exception {
        int databaseSizeBeforeCreate = schedulerRepository.findAll().size();

        // Create the Scheduler
        restSchedulerMockMvc.perform(post("/api/schedulers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scheduler)))
            .andExpect(status().isCreated());

        // Validate the Scheduler in the database
        List<Scheduler> schedulerList = schedulerRepository.findAll();
        assertThat(schedulerList).hasSize(databaseSizeBeforeCreate + 1);
        Scheduler testScheduler = schedulerList.get(schedulerList.size() - 1);
        assertThat(testScheduler.getIdGuildServer()).isEqualTo(DEFAULT_ID_GUILD_SERVER);
        assertThat(testScheduler.getIdChannel()).isEqualTo(DEFAULT_ID_CHANNEL);
        assertThat(testScheduler.isActivated()).isEqualTo(DEFAULT_ACTIVATED);
        assertThat(testScheduler.getHour()).isEqualTo(DEFAULT_HOUR);
    }

    @Test
    @Transactional
    public void createSchedulerWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = schedulerRepository.findAll().size();

        // Create the Scheduler with an existing ID
        scheduler.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSchedulerMockMvc.perform(post("/api/schedulers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scheduler)))
            .andExpect(status().isBadRequest());

        // Validate the Scheduler in the database
        List<Scheduler> schedulerList = schedulerRepository.findAll();
        assertThat(schedulerList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkIdGuildServerIsRequired() throws Exception {
        int databaseSizeBeforeTest = schedulerRepository.findAll().size();
        // set the field null
        scheduler.setIdGuildServer(null);

        // Create the Scheduler, which fails.

        restSchedulerMockMvc.perform(post("/api/schedulers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scheduler)))
            .andExpect(status().isBadRequest());

        List<Scheduler> schedulerList = schedulerRepository.findAll();
        assertThat(schedulerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkIdChannelIsRequired() throws Exception {
        int databaseSizeBeforeTest = schedulerRepository.findAll().size();
        // set the field null
        scheduler.setIdChannel(null);

        // Create the Scheduler, which fails.

        restSchedulerMockMvc.perform(post("/api/schedulers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scheduler)))
            .andExpect(status().isBadRequest());

        List<Scheduler> schedulerList = schedulerRepository.findAll();
        assertThat(schedulerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkActivatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = schedulerRepository.findAll().size();
        // set the field null
        scheduler.setActivated(null);

        // Create the Scheduler, which fails.

        restSchedulerMockMvc.perform(post("/api/schedulers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scheduler)))
            .andExpect(status().isBadRequest());

        List<Scheduler> schedulerList = schedulerRepository.findAll();
        assertThat(schedulerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkHourIsRequired() throws Exception {
        int databaseSizeBeforeTest = schedulerRepository.findAll().size();
        // set the field null
        scheduler.setHour(null);

        // Create the Scheduler, which fails.

        restSchedulerMockMvc.perform(post("/api/schedulers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scheduler)))
            .andExpect(status().isBadRequest());

        List<Scheduler> schedulerList = schedulerRepository.findAll();
        assertThat(schedulerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSchedulers() throws Exception {
        // Initialize the database
        schedulerRepository.saveAndFlush(scheduler);

        // Get all the schedulerList
        restSchedulerMockMvc.perform(get("/api/schedulers?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scheduler.getId().intValue())))
            .andExpect(jsonPath("$.[*].idGuildServer").value(hasItem(DEFAULT_ID_GUILD_SERVER)))
            .andExpect(jsonPath("$.[*].idChannel").value(hasItem(DEFAULT_ID_CHANNEL)))
            .andExpect(jsonPath("$.[*].activated").value(hasItem(DEFAULT_ACTIVATED.booleanValue())))
            .andExpect(jsonPath("$.[*].hour").value(hasItem(DEFAULT_HOUR.intValue())));
    }
    
    @Test
    @Transactional
    public void getScheduler() throws Exception {
        // Initialize the database
        schedulerRepository.saveAndFlush(scheduler);

        // Get the scheduler
        restSchedulerMockMvc.perform(get("/api/schedulers/{id}", scheduler.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(scheduler.getId().intValue()))
            .andExpect(jsonPath("$.idGuildServer").value(DEFAULT_ID_GUILD_SERVER))
            .andExpect(jsonPath("$.idChannel").value(DEFAULT_ID_CHANNEL))
            .andExpect(jsonPath("$.activated").value(DEFAULT_ACTIVATED.booleanValue()))
            .andExpect(jsonPath("$.hour").value(DEFAULT_HOUR.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingScheduler() throws Exception {
        // Get the scheduler
        restSchedulerMockMvc.perform(get("/api/schedulers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateScheduler() throws Exception {
        // Initialize the database
        schedulerRepository.saveAndFlush(scheduler);

        int databaseSizeBeforeUpdate = schedulerRepository.findAll().size();

        // Update the scheduler
        Scheduler updatedScheduler = schedulerRepository.findById(scheduler.getId()).get();
        // Disconnect from session so that the updates on updatedScheduler are not directly saved in db
        em.detach(updatedScheduler);
        updatedScheduler
            .idGuildServer(UPDATED_ID_GUILD_SERVER)
            .idChannel(UPDATED_ID_CHANNEL)
            .activated(UPDATED_ACTIVATED)
            .hour(UPDATED_HOUR);

        restSchedulerMockMvc.perform(put("/api/schedulers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedScheduler)))
            .andExpect(status().isOk());

        // Validate the Scheduler in the database
        List<Scheduler> schedulerList = schedulerRepository.findAll();
        assertThat(schedulerList).hasSize(databaseSizeBeforeUpdate);
        Scheduler testScheduler = schedulerList.get(schedulerList.size() - 1);
        assertThat(testScheduler.getIdGuildServer()).isEqualTo(UPDATED_ID_GUILD_SERVER);
        assertThat(testScheduler.getIdChannel()).isEqualTo(UPDATED_ID_CHANNEL);
        assertThat(testScheduler.isActivated()).isEqualTo(UPDATED_ACTIVATED);
        assertThat(testScheduler.getHour()).isEqualTo(UPDATED_HOUR);
    }

    @Test
    @Transactional
    public void updateNonExistingScheduler() throws Exception {
        int databaseSizeBeforeUpdate = schedulerRepository.findAll().size();

        // Create the Scheduler

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSchedulerMockMvc.perform(put("/api/schedulers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(scheduler)))
            .andExpect(status().isBadRequest());

        // Validate the Scheduler in the database
        List<Scheduler> schedulerList = schedulerRepository.findAll();
        assertThat(schedulerList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteScheduler() throws Exception {
        // Initialize the database
        schedulerRepository.saveAndFlush(scheduler);

        int databaseSizeBeforeDelete = schedulerRepository.findAll().size();

        // Delete the scheduler
        restSchedulerMockMvc.perform(delete("/api/schedulers/{id}", scheduler.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Scheduler> schedulerList = schedulerRepository.findAll();
        assertThat(schedulerList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
