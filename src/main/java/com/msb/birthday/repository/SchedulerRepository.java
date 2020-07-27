package com.msb.birthday.repository;
import com.msb.birthday.domain.Scheduler;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Scheduler entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SchedulerRepository extends JpaRepository<Scheduler, Long> {

}
