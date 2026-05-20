-- MySQL dump 10.13  Distrib 8.4.8, for Linux (x86_64)
--
-- Host: localhost    Database: freelance_db
-- ------------------------------------------------------
-- Server version	8.4.8

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `applications`
--

DROP TABLE IF EXISTS `applications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `applications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `applied_at` datetime(6) DEFAULT NULL,
  `cover_letter` text,
  `proposed_amount_tzs` decimal(12,2) DEFAULT NULL,
  `proposed_timeline` varchar(50) DEFAULT NULL,
  `status` enum('ACCEPTED','PENDING','REJECTED','WITHDRAWN') DEFAULT NULL,
  `freelancer_id` bigint NOT NULL,
  `job_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKh6y11heys6e0vfoqr0dgv7fxq` (`freelancer_id`),
  KEY `FK65weib1lru9dkrbto5pv389vi` (`job_id`),
  CONSTRAINT `FK65weib1lru9dkrbto5pv389vi` FOREIGN KEY (`job_id`) REFERENCES `jobs` (`id`),
  CONSTRAINT `FKh6y11heys6e0vfoqr0dgv7fxq` FOREIGN KEY (`freelancer_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `applications`
--

LOCK TABLES `applications` WRITE;
/*!40000 ALTER TABLE `applications` DISABLE KEYS */;
INSERT INTO `applications` VALUES (1,'2026-04-02 21:09:16.449433','wew',50000.00,'As discussed','PENDING',10,3),(2,'2026-04-02 21:51:05.594538','hhdjdhhdghdgsh',200000.00,'As discussed','PENDING',10,5),(3,'2026-04-02 22:39:25.774291','mmmmmmmmmm',500000.00,'As discussed','PENDING',5,3),(4,'2026-04-06 22:29:53.396869','I want this opportunity',800000.00,'As discussed','PENDING',13,6);
/*!40000 ALTER TABLE `applications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `freelancer_profiles`
--

DROP TABLE IF EXISTS `freelancer_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `freelancer_profiles` (
  `user_id` bigint NOT NULL,
  `average_rating` decimal(3,2) DEFAULT NULL,
  `completed_jobs` int DEFAULT NULL,
  `headline` varchar(300) DEFAULT NULL,
  `hourly_rate_tzs` decimal(10,2) DEFAULT NULL,
  `overview` text,
  `portfolio_url` varchar(500) DEFAULT NULL,
  `primary_category` varchar(120) DEFAULT NULL,
  `profile_picture_url` varchar(255) DEFAULT NULL,
  `total_earnings_tzs` int DEFAULT NULL,
  `years_of_experience` int DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `FKd173yjyeljsmurg3rtgx3yar3` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `freelancer_profiles`
--

LOCK TABLES `freelancer_profiles` WRITE;
/*!40000 ALTER TABLE `freelancer_profiles` DISABLE KEYS */;
/*!40000 ALTER TABLE `freelancer_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jobs`
--

DROP TABLE IF EXISTS `jobs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jobs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `budget_type` varchar(255) DEFAULT NULL,
  `budget_tzs` decimal(12,2) DEFAULT NULL,
  `category` varchar(120) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text,
  `location_preference` varchar(80) DEFAULT NULL,
  `status` enum('CANCELLED','COMPLETED','DISPUTED','IN_PROGRESS','OPEN') DEFAULT NULL,
  `title` varchar(200) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `client_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKeyw4t55kg6xhuagrsaxekvfan` (`client_id`),
  CONSTRAINT `FKeyw4t55kg6xhuagrsaxekvfan` FOREIGN KEY (`client_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jobs`
--

LOCK TABLES `jobs` WRITE;
/*!40000 ALTER TABLE `jobs` DISABLE KEYS */;
INSERT INTO `jobs` VALUES (3,'FIXED',2000000.00,'design','2026-04-02 19:46:48.689597','Refresh the storefront with a modern and mobile-friendly interface.','Remote','OPEN','E-commerce website redesign','2026-04-02 19:46:48.689597',7),(4,'FIXED',3500000.00,'development','2026-04-02 19:46:48.748359','Build an analytics dashboard for a new SaaS offering.','Remote','OPEN','React dashboard build','2026-04-02 19:46:48.748359',7),(5,'FIXED',1200000.00,'marketing','2026-04-02 21:49:14.067476','You ere supposed to come with a working portifilio for BIS','Remote','OPEN','Business information system','2026-04-02 21:49:14.067476',11),(6,'FIXED',20000000.00,'development','2026-04-06 18:59:54.142653','This project is indeed for creating the TMS which will be used by different people in transportation sector','Remote','OPEN','Transport management system','2026-04-06 18:59:54.142653',12);
/*!40000 ALTER TABLE `jobs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `messages`
--

DROP TABLE IF EXISTS `messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text NOT NULL,
  `is_read` bit(1) DEFAULT NULL,
  `sent_at` datetime(6) DEFAULT NULL,
  `receiver_id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt05r0b6n0iis8u7dfna4xdh73` (`receiver_id`),
  KEY `FK4ui4nnwntodh6wjvck53dbk9m` (`sender_id`),
  CONSTRAINT `FK4ui4nnwntodh6wjvck53dbk9m` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKt05r0b6n0iis8u7dfna4xdh73` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `messages`
--

LOCK TABLES `messages` WRITE;
/*!40000 ALTER TABLE `messages` DISABLE KEYS */;
INSERT INTO `messages` VALUES (1,'hello',_binary '\0','2026-04-01 19:28:08.596734',4,5),(2,'how are bro',_binary '\0','2026-04-01 19:31:23.985023',5,4),(3,'hjgh',_binary '\0','2026-04-01 19:44:47.334326',5,4),(4,'hjgh',_binary '\0','2026-04-01 19:44:47.551286',5,4),(5,'hello',_binary '\0','2026-04-02 20:01:51.751419',9,4),(6,'Hey',_binary '\0','2026-04-02 20:02:26.270702',8,9),(7,'Hey',_binary '\0','2026-04-02 20:02:59.313908',4,9),(8,'hello',_binary '\0','2026-04-02 22:26:42.795493',8,4);
/*!40000 ALTER TABLE `messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `message` text,
  `is_read` bit(1) DEFAULT NULL,
  `related_entity_id` bigint DEFAULT NULL,
  `related_entity_type` varchar(255) DEFAULT NULL,
  `title` varchar(120) NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9y21adhxn0ayjhfocscqox7bh` (`user_id`),
  CONSTRAINT `FK9y21adhxn0ayjhfocscqox7bh` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment` text,
  `rating` decimal(3,2) NOT NULL,
  `job_id` bigint NOT NULL,
  `reviewed_user_id` bigint NOT NULL,
  `reviewer_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqnic4bk1c0ljhjkvyrr055m4a` (`job_id`),
  KEY `FK6m1kr6qopmdr9icvailpjx8xf` (`reviewed_user_id`),
  KEY `FKd1isgfajhtdl8mgg29up6mofi` (`reviewer_id`),
  CONSTRAINT `FK6m1kr6qopmdr9icvailpjx8xf` FOREIGN KEY (`reviewed_user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKd1isgfajhtdl8mgg29up6mofi` FOREIGN KEY (`reviewer_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKqnic4bk1c0ljhjkvyrr055m4a` FOREIGN KEY (`job_id`) REFERENCES `jobs` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_skills`
--

DROP TABLE IF EXISTS `user_skills`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_skills` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `proficiency_level` varchar(60) DEFAULT NULL,
  `skill_name` varchar(100) NOT NULL,
  `years_of_experience` int DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKro13if9r7fwkr5115715127ai` (`user_id`),
  CONSTRAINT `FKro13if9r7fwkr5115715127ai` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_skills`
--

LOCK TABLES `user_skills` WRITE;
/*!40000 ALTER TABLE `user_skills` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_skills` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) DEFAULT NULL,
  `bio` text,
  `city` varchar(120) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(180) NOT NULL,
  `email_verified` bit(1) DEFAULT NULL,
  `full_name` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `profile_picture_url` varchar(512) DEFAULT NULL,
  `region` varchar(80) DEFAULT NULL,
  `role_name` varchar(50) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (4,_binary '','',NULL,'2026-04-01 19:04:05.298103','tysonmayenze147@gmail.com',_binary '\0','Tyson mayenze','$2a$12$hOgkGVB9/BA/DOIQrWEVLefrkvAx.FJA3sbc93wM19gqvVcT.NHCS',NULL,'/uploads/profiles/4-ebc1d4aa-44dc-4d74-88fe-e4dd8426b3b0.jpg',NULL,'ROLE_ADMIN','2026-04-01 19:06:14.277984'),(5,_binary '','',NULL,'2026-04-01 19:27:14.355789','philipotyson147@gmail.com',_binary '\0','philipo Tyson','$2a$12$xmL4z3n2T86i5G./.nnADutx0gUpn9PNCEduzQGW.QKk0NVZGi9Gq',NULL,'/uploads/profiles/5-ed55c086-6748-4921-ac8a-4acc9af6068b.jpg',NULL,'ROLE_FREELANCER','2026-04-01 19:29:40.718765'),(6,_binary '','Demo platform administrator.',NULL,'2026-04-02 19:46:44.158721','admin@freelance.com',_binary '\0','Jordan Kemp','$2a$12$kzv8MvM5dameN3B7YLRsL.QGhhMeZBASyNvzfxsZDHwHiSTiQx1qC',NULL,NULL,NULL,'ROLE_ADMIN','2026-04-02 19:46:44.158721'),(7,_binary '','Startup founder and project lead.',NULL,'2026-04-02 19:46:46.539796','client@demo.com',_binary '\0','Nora Patel','$2a$12$.FV7jvY72w3TrShaTGs9Oud92z7MGdlKgrsXK8IifOnxmitzc1qQG',NULL,NULL,NULL,'ROLE_CLIENT','2026-04-02 19:46:46.539796'),(8,_binary '','Creative product designer.',NULL,'2026-04-02 19:46:48.538137','freelancer@demo.com',_binary '\0','Ari Turner','$2a$12$4OVcDLmoRvVvOljMxaQUiOuqR22nv2gew.T/gn.Z5bWpn98CsX5oS',NULL,NULL,NULL,'ROLE_FREELANCER','2026-04-02 19:46:48.538137'),(9,_binary '','',NULL,'2026-04-02 20:00:15.972100','anthonyemma2021@gmail.com',_binary '\0','Emman Antony','$2a$12$lo2c54lc3oMc./1G34.pNOAZVIdLF0vaqgC7E2Y1aq0aSNFEdDBc.',NULL,'/uploads/profiles/9-4ed990dd-399d-4da5-8d79-1d125bc558a9.jpg',NULL,'ROLE_CLIENT','2026-04-02 20:06:39.168642'),(10,_binary '','',NULL,'2026-04-02 21:03:01.862001','semantic@gmail.com',_binary '\0','semantic maduhu','$2a$12$92X9l3V8IlJGlrl3WFozgObhFqfdhtTyLFkOwcBQf2eoPfjXwpTva',NULL,NULL,NULL,'ROLE_FREELANCER','2026-04-02 21:03:01.862001'),(11,_binary '','',NULL,'2026-04-02 21:44:28.156414','syntax@gmail.com',_binary '\0','syntax java','$2a$12$h1XBkKrQiv6pblWS8RFZ9OlSG6ZVqPOfXlAh9plxukgmy2As92egC',NULL,NULL,NULL,'ROLE_CLIENT','2026-04-02 21:44:28.156414'),(12,_binary '','Hey I\'m passionate in mobile application development using java programming',NULL,'2026-04-06 18:51:05.327183','masanja@gmail.com',_binary '\0','Masanja magunia','$2a$12$x9TWuGf8fNDa3f/8W1iNg.pU6ELXDq.d/oVMPSqgfiy.Dw0VOkJuq',NULL,'/uploads/profiles/12-9cee4bd0-743b-446e-aeaa-23165a8ccbf9.jpg',NULL,'ROLE_CLIENT','2026-04-06 22:25:05.836305'),(13,_binary '','',NULL,'2026-04-06 22:26:56.724961','magunia@gmail.com',_binary '\0','Magunia','$2a$12$H.WUiTMxJ8oKbtz/y/3gHOkHt/LHr5JRunpVm2wijj8piHpwiimQC',NULL,NULL,NULL,'ROLE_FREELANCER','2026-04-06 22:26:56.724961');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-20 15:24:34
