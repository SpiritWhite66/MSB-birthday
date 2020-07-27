package com.msb.birthday.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;

/**
 * A Scheduler.
 */
@Entity
@Table(name = "scheduler")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Scheduler implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "id_guild_server", nullable = false, unique = true)
    private String idGuildServer;

    @NotNull
    @Column(name = "id_channel", nullable = false)
    private String idChannel;

    @NotNull
    @Column(name = "activated", nullable = false)
    private Boolean activated;

    @NotNull
    @Column(name = "hour", nullable = false)
    private Long hour;

    @OneToOne
    @JoinColumn(unique = true)
    private Pattern pattern;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdGuildServer() {
        return idGuildServer;
    }

    public Scheduler idGuildServer(String idGuildServer) {
        this.idGuildServer = idGuildServer;
        return this;
    }

    public void setIdGuildServer(String idGuildServer) {
        this.idGuildServer = idGuildServer;
    }

    public String getIdChannel() {
        return idChannel;
    }

    public Scheduler idChannel(String idChannel) {
        this.idChannel = idChannel;
        return this;
    }

    public void setIdChannel(String idChannel) {
        this.idChannel = idChannel;
    }

    public Boolean isActivated() {
        return activated;
    }

    public Scheduler activated(Boolean activated) {
        this.activated = activated;
        return this;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public Long getHour() {
        return hour;
    }

    public Scheduler hour(Long hour) {
        this.hour = hour;
        return this;
    }

    public void setHour(Long hour) {
        this.hour = hour;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Scheduler pattern(Pattern pattern) {
        this.pattern = pattern;
        return this;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Scheduler)) {
            return false;
        }
        return id != null && id.equals(((Scheduler) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Scheduler{" +
            "id=" + getId() +
            ", idGuildServer='" + getIdGuildServer() + "'" +
            ", idChannel='" + getIdChannel() + "'" +
            ", activated='" + isActivated() + "'" +
            ", hour=" + getHour() +
            "}";
    }
}
