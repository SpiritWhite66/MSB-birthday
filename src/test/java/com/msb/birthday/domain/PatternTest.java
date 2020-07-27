package com.msb.birthday.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.msb.birthday.web.rest.TestUtil;

public class PatternTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Pattern.class);
        Pattern pattern1 = new Pattern();
        pattern1.setId(1L);
        Pattern pattern2 = new Pattern();
        pattern2.setId(pattern1.getId());
        assertThat(pattern1).isEqualTo(pattern2);
        pattern2.setId(2L);
        assertThat(pattern1).isNotEqualTo(pattern2);
        pattern1.setId(null);
        assertThat(pattern1).isNotEqualTo(pattern2);
    }
}
