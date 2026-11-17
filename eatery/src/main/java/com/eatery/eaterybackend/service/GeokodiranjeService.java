package com.eatery.eaterybackend.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.eatery.eaterybackend.exception.ApiException;

@Service
public class GeokodiranjeService {

    public record Koordinate(double lat, double lng) {}

    public Koordinate geokodiraj(String adresa) {
        if (adresa == null || adresa.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Adresa je obavezna!");
        }

        try {
            Koordinate pronadjene = pretrazi(adresa.trim());
            if (pronadjene != null) {
                return pronadjene;
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Greška pri određivanju koordinata adrese.");
        }

        throw new ApiException(HttpStatus.BAD_REQUEST,
                "Unesena adresa nije pronađena na mapi. Molimo unesite tačnu adresu.");
    }

    private Koordinate pretrazi(String adresa) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://nominatim.openstreetmap.org/search?format=json&limit=1&q="
                + URLEncoder.encode(adresa, StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "EateryApp/1.0 (kontakt@eatery.ba)");
        headers.set("Accept-Language", "bs,hr,sr");

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {});

        List<Map<String, Object>> rezultati = response.getBody();
        if (rezultati == null || rezultati.isEmpty()) {
            return null;
        }
        Map<String, Object> prvaLokacija = rezultati.get(0);
        return new Koordinate(
                Double.parseDouble(prvaLokacija.get("lat").toString()),
                Double.parseDouble(prvaLokacija.get("lon").toString()));
    }
}
