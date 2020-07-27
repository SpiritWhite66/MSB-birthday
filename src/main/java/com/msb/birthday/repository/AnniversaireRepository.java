package com.msb.birthday.repository;
import com.msb.birthday.domain.Anniversaire;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Anniversaire entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AnniversaireRepository extends JpaRepository<Anniversaire, Long> {

}
