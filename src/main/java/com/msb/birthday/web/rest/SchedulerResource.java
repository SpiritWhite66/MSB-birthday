package com.msb.birthday.web.rest;

import com.msb.birthday.domain.Scheduler;
import com.msb.birthday.repository.SchedulerRepository;
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
 * REST controller for managing {@link com.msb.birthday.domain.Scheduler}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SchedulerResource {

    private final Logger log = LoggerFactory.getLogger(SchedulerResource.class);

    private static final String ENTITY_NAME = "birthdayScheduler";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SchedulerRepository schedulerRepository;

    public SchedulerResource(SchedulerRepository schedulerRepository) {
        this.schedulerRepository = schedulerRepository;
    }

    /**
     * {@code POST  /schedulers} : Create a new scheduler.
     *
     * @param scheduler the scheduler to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new scheduler, or with status {@code 400 (Bad Request)} if the scheduler has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/schedulers")
    public ResponseEntity<Scheduler> createScheduler(@Valid @RequestBody Scheduler scheduler) throws URISyntaxException {
        log.debug("REST request to save Scheduler : {}", scheduler);
        if (scheduler.getId() != null) {
            throw new BadRequestAlertException("A new scheduler cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Scheduler result = schedulerRepository.save(scheduler);
        return ResponseEntity.created(new URI("/api/schedulers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /schedulers} : Updates an existing scheduler.
     *
     * @param scheduler the scheduler to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scheduler,
     * or with status {@code 400 (Bad Request)} if the scheduler is not valid,
     * or with status {@code 500 (Internal Server Error)} if the scheduler couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/schedulers")
    public ResponseEntity<Scheduler> updateScheduler(@Valid @RequestBody Scheduler scheduler) throws URISyntaxException {
        log.debug("REST request to update Scheduler : {}", scheduler);
        if (scheduler.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Scheduler result = schedulerRepository.save(scheduler);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, scheduler.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /schedulers} : get all the schedulers.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of schedulers in body.
     */
    @GetMapping("/schedulers")
    public List<Scheduler> getAllSchedulers() {
        log.debug("REST request to get all Schedulers");
        return schedulerRepository.findAll();
    }

    /**
     * {@code GET  /schedulers/:id} : get the "id" scheduler.
     *
     * @param id the id of the scheduler to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the scheduler, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/schedulers/{id}")
    public ResponseEntity<Scheduler> getScheduler(@PathVariable Long id) {
        log.debug("REST request to get Scheduler : {}", id);
        Optional<Scheduler> scheduler = schedulerRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(scheduler);
    }

    /**
     * {@code DELETE  /schedulers/:id} : delete the "id" scheduler.
     *
     * @param id the id of the scheduler to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/schedulers/{id}")
    public ResponseEntity<Void> deleteScheduler(@PathVariable Long id) {
        log.debug("REST request to delete Scheduler : {}", id);
        schedulerRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
