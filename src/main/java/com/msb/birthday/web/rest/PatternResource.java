package com.msb.birthday.web.rest;

import com.msb.birthday.domain.Pattern;
import com.msb.birthday.repository.PatternRepository;
import com.msb.birthday.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional; 
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.msb.birthday.domain.Pattern}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PatternResource {

    private final Logger log = LoggerFactory.getLogger(PatternResource.class);

    private static final String ENTITY_NAME = "birthdayPattern";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PatternRepository patternRepository;

    public PatternResource(PatternRepository patternRepository) {
        this.patternRepository = patternRepository;
    }

    /**
     * {@code POST  /patterns} : Create a new pattern.
     *
     * @param pattern the pattern to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pattern, or with status {@code 400 (Bad Request)} if the pattern has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/patterns")
    public ResponseEntity<Pattern> createPattern(@Valid @RequestBody Pattern pattern) throws URISyntaxException {
        log.debug("REST request to save Pattern : {}", pattern);
        if (pattern.getId() != null) {
            throw new BadRequestAlertException("A new pattern cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Pattern result = patternRepository.save(pattern);
        return ResponseEntity.created(new URI("/api/patterns/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /patterns} : Updates an existing pattern.
     *
     * @param pattern the pattern to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pattern,
     * or with status {@code 400 (Bad Request)} if the pattern is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pattern couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/patterns")
    public ResponseEntity<Pattern> updatePattern(@Valid @RequestBody Pattern pattern) throws URISyntaxException {
        log.debug("REST request to update Pattern : {}", pattern);
        if (pattern.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Pattern result = patternRepository.save(pattern);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pattern.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /patterns} : get all the patterns.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of patterns in body.
     */
    @GetMapping("/patterns")
    public List<Pattern> getAllPatterns() {
        log.debug("REST request to get all Patterns");
        return patternRepository.findAll();
    }

    /**
     * {@code GET  /patterns/:id} : get the "id" pattern.
     *
     * @param id the id of the pattern to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pattern, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/patterns/{id}")
    public ResponseEntity<Pattern> getPattern(@PathVariable Long id) {
        log.debug("REST request to get Pattern : {}", id);
        Optional<Pattern> pattern = patternRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(pattern);
    }

    /**
     * {@code DELETE  /patterns/:id} : delete the "id" pattern.
     *
     * @param id the id of the pattern to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/patterns/{id}")
    public ResponseEntity<Void> deletePattern(@PathVariable Long id) {
        log.debug("REST request to delete Pattern : {}", id);
        patternRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
