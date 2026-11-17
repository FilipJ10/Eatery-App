
CREATE TABLE IF NOT EXISTS `eatery_db`.`recenzija` (
  `id_recenzije` BIGINT NOT NULL AUTO_INCREMENT,
  `ocjena` INT NOT NULL,                         
  `komentar` VARCHAR(1000) NULL DEFAULT NULL,       
  `datum_kreiranja` DATETIME(6) NOT NULL,
  `id_kupca` BIGINT NOT NULL,                      
  `id_restorana` BIGINT NOT NULL,                  
  `id_narudzbe` BIGINT NOT NULL,                   
  PRIMARY KEY (`id_recenzije`),
  UNIQUE INDEX `UK_recenzija_narudzba` (`id_narudzbe` ASC) VISIBLE, -- Jedna narudžba = jedna recenzija
  INDEX `FK_recenzija_kupac` (`id_kupca` ASC) VISIBLE,
  INDEX `FK_recenzija_restoran` (`id_restorana` ASC) VISIBLE,
  CONSTRAINT `FK_recenzija_kupac`
    FOREIGN KEY (`id_kupca`)
    REFERENCES `eatery_db`.`korisnik` (`id_korisnika`),
  CONSTRAINT `FK_recenzija_restoran`
    FOREIGN KEY (`id_restorana`)
    REFERENCES `eatery_db`.`korisnik` (`id_korisnika`),
  CONSTRAINT `FK_recenzija_narudzba`
    FOREIGN KEY (`id_narudzbe`)
    REFERENCES `eatery_db`.`narudzba` (`id_narudzbe`)
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;

-- 16.11.2026: uklonjena kolona narudzba.id_klijenta (duplikat kolone id_restorana).

ALTER TABLE `eatery_db`.`narudzba` DROP COLUMN `id_klijenta`;

-- 16.11.2026: uklonjene tabele ponuda i slika i kolona narudzba.id_ponude (nisu se koristile u kodu).
-- Pokrenuti nakon povlačenja koda bez SlikaEntity, PonudaEntity, PonudaRepository i polja idPonude
-- u NarudzbaEntity, i nakon restarta backenda.
ALTER TABLE `eatery_db`.`narudzba` DROP COLUMN `id_ponude`;
DROP TABLE `eatery_db`.`ponuda`;

-- Kolona korisnik.id_slike ima strani ključ prema tabeli slika.
SET @fk_slika = (
  SELECT CONSTRAINT_NAME FROM information_schema.KEY_COLUMN_USAGE
  WHERE TABLE_SCHEMA = 'eatery_db' AND TABLE_NAME = 'korisnik'
    AND COLUMN_NAME = 'id_slike' AND REFERENCED_TABLE_NAME = 'slika'
  LIMIT 1
);
SET @upit = CONCAT('ALTER TABLE `eatery_db`.`korisnik` DROP FOREIGN KEY `', @fk_slika, '`');
PREPARE naredba FROM @upit;
EXECUTE naredba;
DEALLOCATE PREPARE naredba;

ALTER TABLE `eatery_db`.`korisnik` DROP COLUMN `id_slike`;
DROP TABLE `eatery_db`.`slika`;
