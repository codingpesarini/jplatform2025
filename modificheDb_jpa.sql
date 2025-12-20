-- ====================================
-- JPLATFORM - Rinomina colonne Site
-- Normalizza nomi contenutiFront
-- ====================================

USE jplatform;

-- Slot 01
ALTER TABLE site CHANGE COLUMN contenutiFront1 contenutiFront01 VARCHAR(255);
ALTER TABLE site CHANGE COLUMN contenutiOrdineFront1 contenutiOrdineFront01 VARCHAR(255);
ALTER TABLE site CHANGE COLUMN maxContenutiFront1 maxContenutiFront01 VARCHAR(255);

-- Slot 02
ALTER TABLE site CHANGE COLUMN contenutiFront2 contenutiFront02 VARCHAR(255);
ALTER TABLE site CHANGE COLUMN contenutiOrdineFront2 contenutiOrdineFront02 VARCHAR(255);
ALTER TABLE site CHANGE COLUMN maxContenutiFront2 maxContenutiFront02 VARCHAR(255);

-- Slot 03
ALTER TABLE site CHANGE COLUMN contenutiFront3 contenutiFront03 VARCHAR(255);
ALTER TABLE site CHANGE COLUMN contenutiOrdineFront3 contenutiOrdineFront03 VARCHAR(255);
ALTER TABLE site CHANGE COLUMN maxContenutiFront3 maxContenutiFront03 VARCHAR(255);

-- Slot 04
ALTER TABLE site CHANGE COLUMN contenutiFront4 contenutiFront04 VARCHAR(255);
ALTER TABLE site CHANGE COLUMN contenutiOrdineFront4 contenutiOrdineFront04 VARCHAR(255);
ALTER TABLE site CHANGE COLUMN maxContenutiFront4 maxContenutiFront04 VARCHAR(255);

-- Slot 05
ALTER TABLE site CHANGE COLUMN contenutiFront5 contenutiFront05 VARCHAR(255);
ALTER TABLE site CHANGE COLUMN contenutiOrdineFront5 contenutiOrdineFront05 VARCHAR(255);
ALTER TABLE site CHANGE COLUMN maxContenutiFront5 maxContenutiFront05 VARCHAR(255);

-- Slot 06
ALTER TABLE site CHANGE COLUMN contenutiFront6 contenutiFront06 VARCHAR(255);
ALTER TABLE site CHANGE COLUMN contenutiOrdineFront6 contenutiOrdineFront06 VARCHAR(255);
ALTER TABLE site CHANGE COLUMN maxContenutiFront6 maxContenutiFront06 VARCHAR(255);

-- Slot 07
ALTER TABLE site CHANGE COLUMN contenutiFront7 contenutiFront07 VARCHAR(255);
ALTER TABLE site CHANGE COLUMN contenutiOrdineFront7 contenutiOrdineFront07 VARCHAR(255);
ALTER TABLE site CHANGE COLUMN maxContenutiFront7 maxContenutiFront07 VARCHAR(255);

-- Slot 08
ALTER TABLE site CHANGE COLUMN contenutiFront8 contenutiFront08 VARCHAR(255);
ALTER TABLE site CHANGE COLUMN contenutiOrdineFront8 contenutiOrdineFront08 VARCHAR(255);
ALTER TABLE site CHANGE COLUMN maxContenutiFront8 maxContenutiFront08 VARCHAR(255);

-- Slot 09
ALTER TABLE site CHANGE COLUMN contenutiFront9 contenutiFront09 VARCHAR(255);
ALTER TABLE site CHANGE COLUMN contenutiOrdineFront9 contenutiOrdineFront09 VARCHAR(255);
ALTER TABLE site CHANGE COLUMN maxContenutiFront9 maxContenutiFront09 VARCHAR(255);

-- Slot 10
-- Nota: il 10 è già corretto (contenutiFront10) quindi non serve cambiare
-- Ma per consistenza con gli altri, possiamo comunque eseguirlo
ALTER TABLE site CHANGE COLUMN contenutiFront10 contenutiFront10 VARCHAR(255);
ALTER TABLE site CHANGE COLUMN contenutiOrdineFront10 contenutiOrdineFront10 VARCHAR(255);
ALTER TABLE site CHANGE COLUMN maxContenutiFront10 maxContenutiFront10 VARCHAR(255);