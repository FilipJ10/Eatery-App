package com.eatery.eaterybackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "jelo")
@Getter
@Setter
public class JeloEntity {

    private static final ZoneId ZONA = ZoneId.of("Europe/Sarajevo");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_jela")
    private Long id;

    @Column(nullable = false)
    private String naziv;

    private String opis;

    @Column(nullable = false)
    private BigDecimal cijena;

    /** Popust u procentima (npr. 20 = 20%). Null ili 0 = nema popusta. */
    @Column(name = "popust_procenat", precision = 5, scale = 2)
    private BigDecimal popustProcenat;

    /** Početak važenja popusta (uključivo). */
    @Column(name = "popust_od")
    private LocalDateTime popustOd;

    /** Kraj važenja popusta (uključivo do tog trenutka). */
    @Column(name = "popust_do")
    private LocalDateTime popustDo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_kategorije", nullable = false)
    private KategorijaEntity kategorija;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_korisnika", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "sifra"})
    @JsonIgnore
    private KorisnikEntity restoran;

    /**
     * Da li je popust trenutno aktivan (u periodu i procenat > 0).
     */
    @Transient
    public boolean isPopustAktivan() {
        if (popustProcenat == null || popustProcenat.signum() <= 0) {
            return false;
        }
        if (popustOd == null || popustDo == null) {
            return false;
        }
        LocalDateTime sada = LocalDateTime.now(ZONA);
        return !sada.isBefore(popustOd) && !sada.isAfter(popustDo);
    }

    /**
     * Efektivna cijena: sa popustom ako je aktivan, inače originalna.
     */
    @Transient
    public BigDecimal getEfektivnaCijena() {
        if (!isPopustAktivan() || cijena == null) {
            return cijena;
        }
        BigDecimal faktor = BigDecimal.ONE.subtract(
                popustProcenat.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
        );
        return cijena.multiply(faktor).setScale(2, RoundingMode.HALF_UP);
    }
}
