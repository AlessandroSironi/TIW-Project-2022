-- phpMyAdmin SQL Dump
-- version 4.9.5deb2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Creato il: Mag 02, 2022 alle 10:06
-- Versione del server: 8.0.28-0ubuntu0.20.04.3
-- Versione PHP: 7.4.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `TIW-Project`
--
CREATE DATABASE IF NOT EXISTS `TIW-Project` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `TIW-Project`;

-- --------------------------------------------------------

--
-- Struttura della tabella `Invitation`
--

DROP TABLE IF EXISTS `Invitation`;
CREATE TABLE IF NOT EXISTS `Invitation` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `IDMeeting` int NOT NULL,
  `IDUser` int NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `IDMeeting` (`IDMeeting`),
  KEY `IDUser` (`IDUser`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dump dei dati per la tabella `Invitation`
--

INSERT INTO `Invitation` (`ID`, `IDMeeting`, `IDUser`) VALUES
(1, 1, 2),
(2, 1, 3),
(3, 1, 4),
(4, 2, 2),
(5, 2, 4),
(6, 3, 2),
(7, 3, 3),
(8, 3, 5),
(9, 3, 6),
(10, 3, 7),
(11, 3, 8),
(12, 4, 1),
(13, 4, 5),
(14, 5, 1),
(15, 5, 3),
(16, 5, 6),
(17, 5, 7),
(18, 5, 8);

-- --------------------------------------------------------

--
-- Struttura della tabella `Meeting`
--

DROP TABLE IF EXISTS `Meeting`;
CREATE TABLE IF NOT EXISTS `Meeting` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `ID_Creator` int NOT NULL,
  `title` varchar(100) NOT NULL,
  `startDate` datetime NOT NULL,
  `duration` int NOT NULL,
  `capacity` int NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dump dei dati per la tabella `Meeting`
--

INSERT INTO `Meeting` (`ID`, `ID_Creator`, `title`, `startDate`, `duration`, `capacity`) VALUES
(1, 1, 'Presentazione Progetto TIW', '2022-05-25 16:30:00', 60, 3),
(2, 1, 'Sviluppo Progetto TIW', '2022-05-24 09:30:00', 75, 2),
(3, 1, 'TIW - Lezione', '2022-05-26 11:15:00', 90, 300),
(4, 2, 'Progetto Ing. SW', '2022-05-27 15:30:00', 120, 2),
(5, 2, 'Progetto Ing. Inf', '2022-05-27 14:30:00', 120, 5);

-- --------------------------------------------------------

--
-- Struttura della tabella `User`
--

DROP TABLE IF EXISTS `User`;
CREATE TABLE IF NOT EXISTS `User` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `mail` varchar(100) NOT NULL,
  `user` varchar(100) NOT NULL,
  `psw_hash` varbinary(128) NOT NULL,
  `psw_salt` varbinary(16) NOT NULL,
  `name` varchar(100) NOT NULL,
  `surname` varchar(100) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dump dei dati per la tabella `User`
--

INSERT INTO `User` (`ID`, `mail`, `user`, `psw_hash`, `psw_salt`, `name`, `surname`) VALUES
(1, 'alessandro1.sironi@mail.polimi.it', 'alessandro', 0xa31212a1ec8e96fdbb6f1aedabd0bfc0, 0xdeb56525f2f709c3dfb5b8646b68ec50, 'Alessandro', 'Sironi'),
(2, 'chiara.bianchi@mail.polimi.it', 'chiara', 0xf97f1bae433e2730aaafd3eb6550fff7, 0x3a55b35a5e29dee7e314157b24b6e9b2, 'Chiara', 'Bianchi'),
(3, 'giuseppe.verdi@mail.polimi.it', 'giuseppe', 0xafe0b739a058b5befc72e8d54b21cf06, 0xf9b15d8103cfcff377ef0ff12b9d258f, 'Giuseppe', 'Verdi'),
(4, 'francesco.rossi@polimi.it', 'francesco', 0x2d440d42a849543654176a71036cb308, 0xe34a81fa1363277f49dace2660f457a9, 'Francesco', 'Rossi'),
(5, 'cristina.bianchi@polimi.it', 'cristina', 0x1d84a6c920ad98cc3b6ba4b9cd15ba09, 0xe0f080eccdefb46b95f8a92a9e2c37bb, 'Cristina', 'Bianchi'),
(6, 'vanessa.novara@polimi.it', 'vanessa', 0xad8775facbe61f4656405be189af019b, 0xbd2a15ad45719c38bdf97fb11bd3f2dd, 'Vanessa', 'Novara'),
(7, 'filippo.neri@polimi.it', 'filippo', 0x3158d1da6f5f6653fdac059276c0e753, 0x6df529e4ca3beeabe7242babaa7d45c0, 'Filippo', 'Neri'),
(8, 'giulia.gialli@mail.polimi.it', 'giulia', 0x57dc7caee73b2a9a9f20e39f3a2099e4, 0x6644872cb4d54a4a30506c1080db3d65, 'Giulia', 'Gialli'),
(9, 'maurizio.blu@mail.polimi.it', 'maurizio', 0x20c1da11b960fb5867a3a98576f43762, 0xf1cf9e45334754fcfdc86ba638eb33ab, 'Maurizio', 'Blu');

--
-- Limiti per le tabelle scaricate
--

--
-- Limiti per la tabella `Invitation`
--
ALTER TABLE `Invitation`
  ADD CONSTRAINT `Invitation_ibfk_1` FOREIGN KEY (`IDMeeting`) REFERENCES `Meeting` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `Invitation_ibfk_2` FOREIGN KEY (`IDUser`) REFERENCES `User` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
