package com.msb.birthday.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.msb.birthday.web.rest.TestUtil;

public class SchedulerTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Scheduler.class);
        Scheduler scheduler1 = new Scheduler();
        scheduler1.setId(1L);
        Scheduler scheduler2 = new Scheduler();
        scheduler2.setId(scheduler1.getId());
        assertThat(scheduler1).isEqualTo(scheduler2);
        scheduler2.setId(2L);
        assertThat(scheduler1).isNotEqualTo(scheduler2);
        scheduler1.setId(null);
        assertThat(scheduler1).isNotEqualTo(scheduler2);
    }
}
