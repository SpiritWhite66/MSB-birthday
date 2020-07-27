package com.msb.birthday.web.rest;

import com.msb.birthday.BirthdayApp;
import com.msb.birthday.domain.Anniversaire;
import com.msb.birthday.repository.AnniversaireRepository;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.msb.birthday.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link AnniversaireResource} REST controller.
 */
@SpringBootTest(classes = BirthdayApp.class)
public class AnniversaireResourceIT {

    private static final String DEFAULT_ID_USER = "AAAAAAAAAA";
    private static final String UPDATED_ID_USER = "BBBBBBBBBB";

    private static final String DEFAULT_ID_GUILD_SERVER = "AAAAAAAAAA";
    private static final String UPDATED_ID_GUILD_SERVER = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_ANNIVERSAIRE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_ANNIVERSAIRE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private AnniversaireRepository anniversaireRepository;

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

    private MockMvc restAnniversaireMockMvc;

    private Anniversaire anniversaire;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AnniversaireResource anniversaireResource = new AnniversaireResource(anniversaireRepository);
        this.restAnniversaireMockMvc = MockMvcBuilders.standaloneSetup(anniversaireResource)
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
    public static Anniversaire createEntity(EntityManager em) {
        Anniversaire anniversaire = new Anniversaire()
            .idUser(DEFAULT_ID_USER)
            .idGuildServer(DEFAULT_ID_GUILD_SERVER)
            .dateAnniversaire(DEFAULT_DATE_ANNIVERSAIRE);
        return anniversaire;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Anniversaire createUpdatedEntity(EntityManager em) {
        Anniversaire anniversaire = new Anniversaire()
            .idUser(UPDATED_ID_USER)
            .idGuildServer(UPDATED_ID_GUILD_SERVER)
            .dateAnniversaire(UPDATED_DATE_ANNIVERSAIRE);
        return anniversaire;
    }

    @BeforeEach
    public void initTest() {
        anniversaire = createEntity(em);
    }

    @Test
    @Transactional
    public void createAnniversaire() throws Exception {
        int databaseSizeBeforeCreate = anniversaireRepository.findAll().size();

        // Create the Anniversaire
        restAnniversaireMockMvc.perform(post("/api/anniversaires")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(anniversaire)))
            .andExpect(status().isCreated());

        // Validate the Anniversaire in the database
        List<Anniversaire> anniversaireList = anniversaireRepository.findAll();
        assertThat(anniversaireList).hasSize(databaseSizeBeforeCreate + 1);
        Anniversaire testAnniversaire = anniversaireList.get(anniversaireList.size() - 1);
        assertThat(testAnniversaire.getIdUser()).isEqualTo(DEFAULT_ID_USER);
        assertThat(testAnniversaire.getIdGuildServer()).isEqualTo(DEFAULT_ID_GUILD_SERVER);
        assertThat(testAnniversaire.getDateAnniversaire()).isEqualTo(DEFAULT_DATE_ANNIVERSAIRE);
    }

    @Test
    @Transactional
    public void createAnniversaireWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = anniversaireRepository.findAll().size();

        // Create the Anniversaire with an existing ID
        anniversaire.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAnniversaireMockMvc.perform(post("/api/anniversaires")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(anniversaire)))
            .andExpect(status().isBadRequest());

        // Validate the Anniversaire in the database
        List<Anniversaire> anniversaireList = anniversaireRepository.findAll();
        assertThat(anniversaireList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkIdUserIsRequired() throws Exception {
        int databaseSizeBeforeTest = anniversaireRepository.findAll().size();
        // set the field null
        anniversaire.setIdUser(null);

        // Create the Anniversaire, which fails.

        restAnniversaireMockMvc.perform(post("/api/anniversaires")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(anniversaire)))
            .andExpect(status().isBadRequest());

        List<Anniversaire> anniversaireList = anniversaireRepository.findAll();
        assertThat(anniversaireList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkIdGuildServerIsRequired() throws Exception {
        int databaseSizeBeforeTest = anniversaireRepository.findAll().size();
        // set the field null
        anniversaire.setIdGuildServer(null);

        // Create the Anniversaire, which fails.

        restAnniversaireMockMvc.perform(post("/api/anniversaires")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(anniversaire)))
            .andExpect(status().isBadRequest());

        List<Anniversaire> anniversaireList = anniversaireRepository.findAll();
        assertThat(anniversaireList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAnniversaires() throws Exception {
        // Initialize the database
        anniversaireRepository.saveAndFlush(anniversaire);

        // Get all the anniversaireList
        restAnniversaireMockMvc.perform(get("/api/anniversaires?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(anniversaire.getId().intValue())))
            .andExpect(jsonPath("$.[*].idUser").value(hasItem(DEFAULT_ID_USER)))
            .andExpect(jsonPath("$.[*].idGuildServer").value(hasItem(DEFAULT_ID_GUILD_SERVER)))
            .andExpect(jsonPath("$.[*].dateAnniversaire").value(hasItem(DEFAULT_DATE_ANNIVERSAIRE.toString())));
    }
    
    @Test
    @Transactional
    public void getAnniversaire() throws Exception {
        // Initialize the database
        anniversaireRepository.saveAndFlush(anniversaire);

        // Get the anniversaire
        restAnniversaireMockMvc.perform(get("/api/anniversaires/{id}", anniversaire.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(anniversaire.getId().intValue()))
            .andExpect(jsonPath("$.idUser").value(DEFAULT_ID_USER))
            .andExpect(jsonPath("$.idGuildServer").value(DEFAULT_ID_GUILD_SERVER))
            .andExpect(jsonPath("$.dateAnniversaire").value(DEFAULT_DATE_ANNIVERSAIRE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAnniversaire() throws Exception {
        // Get the anniversaire
        restAnniversaireMockMvc.perform(get("/api/anniversaires/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAnniversaire() throws Exception {
        // Initialize the database
        anniversaireRepository.saveAndFlush(anniversaire);

        int databaseSizeBeforeUpdate = anniversaireRepository.findAll().size();

        // Update the anniversaire
        Anniversaire updatedAnniversaire = anniversaireRepository.findById(anniversaire.getId()).get();
        // Disconnect from session so that the updates on updatedAnniversaire are not directly saved in db
        em.detach(updatedAnniversaire);
        updatedAnniversaire
            .idUser(UPDATED_ID_USER)
            .idGuildServer(UPDATED_ID_GUILD_SERVER)
            .dateAnniversaire(UPDATED_DATE_ANNIVERSAIRE);

        restAnniversaireMockMvc.perform(put("/api/anniversaires")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAnniversaire)))
            .andExpect(status().isOk());

        // Validate the Anniversaire in the database
        List<Anniversaire> anniversaireList = anniversaireRepository.findAll();
        assertThat(anniversaireList).hasSize(databaseSizeBeforeUpdate);
        Anniversaire testAnniversaire = anniversaireList.get(anniversaireList.size() - 1);
        assertThat(testAnniversaire.getIdUser()).isEqualTo(UPDATED_ID_USER);
        assertThat(testAnniversaire.getIdGuildServer()).isEqualTo(UPDATED_ID_GUILD_SERVER);
        assertThat(testAnniversaire.getDateAnniversaire()).isEqualTo(UPDATED_DATE_ANNIVERSAIRE);
    }

    @Test
    @Transactional
    public void updateNonExistingAnniversaire() throws Exception {
        int databaseSizeBeforeUpdate = anniversaireRepository.findAll().size();

        // Create the Anniversaire

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAnniversaireMockMvc.perform(put("/api/anniversaires")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(anniversaire)))
            .andExpect(status().isBadRequest());

        // Validate the Anniversaire in the database
        List<Anniversaire> anniversaireList = anniversaireRepository.findAll();
        assertThat(anniversaireList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteAnniversaire() throws Exception {
        // Initialize the database
        anniversaireRepository.saveAndFlush(anniversaire);

        int databaseSizeBeforeDelete = anniversaireRepository.findAll().size();

        // Delete the anniversaire
        restAnniversaireMockMvc.perform(delete("/api/anniversaires/{id}", anniversaire.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Anniversaire> anniversaireList = anniversaireRepository.findAll();
        assertThat(anniversaireList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
