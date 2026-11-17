package com.eatery.eaterybackend.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class JeloDTO {
    private String naziv;
    private String opis;
    private BigDecimal cijena;
    private Long kategorijaId;

    /** Popust u procentima (0–100). Null ili 0 = bez popusta. */
    private BigDecimal popustProcenat;

    /** Početak perioda važenja popusta. */
    private LocalDateTime popustOd;

    /** Kraj perioda važenja popusta. */
    private LocalDateTime popustDo;
}
