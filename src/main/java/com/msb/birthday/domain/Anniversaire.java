package com.msb.birthday.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * A Anniversaire.
 */
@Entity
@Table(name = "anniversaire")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Anniversaire implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "id_user", nullable = false)
    private String idUser;

    @NotNull
    @Column(name = "id_guild_server", nullable = false)
    private String idGuildServer;

    @Column(name = "date_anniversaire")
    private Instant dateAnniversaire;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public Anniversaire idUser(String idUser) {
        this.idUser = idUser;
        return this;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdGuildServer() {
        return idGuildServer;
    }

    public Anniversaire idGuildServer(String idGuildServer) {
        this.idGuildServer = idGuildServer;
        return this;
    }

    public void setIdGuildServer(String idGuildServer) {
        this.idGuildServer = idGuildServer;
    }

    public Instant getDateAnniversaire() {
        return dateAnniversaire;
    }

    public Anniversaire dateAnniversaire(Instant dateAnniversaire) {
        this.dateAnniversaire = dateAnniversaire;
        return this;
    }

    public void setDateAnniversaire(Instant dateAnniversaire) {
        this.dateAnniversaire = dateAnniversaire;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Anniversaire)) {
            return false;
        }
        return id != null && id.equals(((Anniversaire) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Anniversaire{" +
            "id=" + getId() +
            ", idUser='" + getIdUser() + "'" +
            ", idGuildServer='" + getIdGuildServer() + "'" +
            ", dateAnniversaire='" + getDateAnniversaire() + "'" +
            "}";
    }
}
