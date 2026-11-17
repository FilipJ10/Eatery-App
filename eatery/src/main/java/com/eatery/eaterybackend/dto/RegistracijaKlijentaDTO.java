package com.eatery.eaterybackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistracijaKlijentaDTO {
    private String korisnickoIme;
    private String sifra;
    private String email;
    private String nazivObjekta;
    private String opis;
    private String adresa;
    private Double lat;
    private Double lng;
    private String radnoVrijemeOd;
    private String radnoVrijemeDo;
}