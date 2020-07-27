package com.msb.birthday.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.msb.birthday.web.rest.TestUtil;

public class AnniversaireTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Anniversaire.class);
        Anniversaire anniversaire1 = new Anniversaire();
        anniversaire1.setId(1L);
        Anniversaire anniversaire2 = new Anniversaire();
        anniversaire2.setId(anniversaire1.getId());
        assertThat(anniversaire1).isEqualTo(anniversaire2);
        anniversaire2.setId(2L);
        assertThat(anniversaire1).isNotEqualTo(anniversaire2);
        anniversaire1.setId(null);
        assertThat(anniversaire1).isNotEqualTo(anniversaire2);
    }
}
