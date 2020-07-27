package com.msb.birthday.web.rest;

import com.msb.birthday.domain.Anniversaire;
import com.msb.birthday.repository.AnniversaireRepository;
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
 * REST controller for managing {@link com.msb.birthday.domain.Anniversaire}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AnniversaireResource {

    private final Logger log = LoggerFactory.getLogger(AnniversaireResource.class);

    private static final String ENTITY_NAME = "birthdayAnniversaire";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AnniversaireRepository anniversaireRepository;

    public AnniversaireResource(AnniversaireRepository anniversaireRepository) {
        this.anniversaireRepository = anniversaireRepository;
    }

    /**
     * {@code POST  /anniversaires} : Create a new anniversaire.
     *
     * @param anniversaire the anniversaire to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new anniversaire, or with status {@code 400 (Bad Request)} if the anniversaire has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/anniversaires")
    public ResponseEntity<Anniversaire> createAnniversaire(@Valid @RequestBody Anniversaire anniversaire) throws URISyntaxException {
        log.debug("REST request to save Anniversaire : {}", anniversaire);
        if (anniversaire.getId() != null) {
            throw new BadRequestAlertException("A new anniversaire cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Anniversaire result = anniversaireRepository.save(anniversaire);
        return ResponseEntity.created(new URI("/api/anniversaires/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /anniversaires} : Updates an existing anniversaire.
     *
     * @param anniversaire the anniversaire to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated anniversaire,
     * or with status {@code 400 (Bad Request)} if the anniversaire is not valid,
     * or with status {@code 500 (Internal Server Error)} if the anniversaire couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/anniversaires")
    public ResponseEntity<Anniversaire> updateAnniversaire(@Valid @RequestBody Anniversaire anniversaire) throws URISyntaxException {
        log.debug("REST request to update Anniversaire : {}", anniversaire);
        if (anniversaire.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Anniversaire result = anniversaireRepository.save(anniversaire);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, anniversaire.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /anniversaires} : get all the anniversaires.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of anniversaires in body.
     */
    @GetMapping("/anniversaires")
    public List<Anniversaire> getAllAnniversaires() {
        log.debug("REST request to get all Anniversaires");
        return anniversaireRepository.findAll();
    }

    /**
     * {@code GET  /anniversaires/:id} : get the "id" anniversaire.
     *
     * @param id the id of the anniversaire to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the anniversaire, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/anniversaires/{id}")
    public ResponseEntity<Anniversaire> getAnniversaire(@PathVariable Long id) {
        log.debug("REST request to get Anniversaire : {}", id);
        Optional<Anniversaire> anniversaire = anniversaireRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(anniversaire);
    }

    /**
     * {@code DELETE  /anniversaires/:id} : delete the "id" anniversaire.
     *
     * @param id the id of the anniversaire to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/anniversaires/{id}")
    public ResponseEntity<Void> deleteAnniversaire(@PathVariable Long id) {
        log.debug("REST request to delete Anniversaire : {}", id);
        anniversaireRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
