CREATE TABLE `kategoria` (`id` INT NOT NULL AUTO_INCREMENT PRYMARY KEY,`nev` VARCHAR(125) NOT NULL);

INSERT INTO kategoria(`nev`) SELECT DISTINCT `etlap`.`kategoria` FROM `etlap`;

ALTER TABLE `etlap` ADD COLUMN `kategoria_id` INT NOT NULL;

UPDATE `etlap`
JOIN `kategoria`
ON `kategoria`.`nev` = `etlap`.`kategoria`
SET `etlap`.`kategoria_id` = `kategoria`.`id`;

ALTER TABLE `etlap` DROP COLUMN `kategoria`;
