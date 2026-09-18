-- MySQL dump 10.13  Distrib 8.0.26, for Win64 (x86_64)
--
-- Host: localhost    Database: job_mgt
-- ------------------------------------------------------
-- Server version	8.0.26

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
-- Current Database: `job_mgt`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `job_mgt` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `job_mgt`;

--
-- Table structure for table `applications`
--

DROP TABLE IF EXISTS `applications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `applications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `applied_at` datetime(6) DEFAULT NULL,
  `job_id` bigint NOT NULL,
  `status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `student_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `applications`
--

LOCK TABLES `applications` WRITE;
/*!40000 ALTER TABLE `applications` DISABLE KEYS */;
INSERT INTO `applications` VALUES (1,NULL,1,'ACCEPTED',3),(2,NULL,2,'ACCEPTED',4),(3,NULL,3,'ACCEPTED',5),(4,NULL,4,'ACCEPTED',6),(5,NULL,5,'APPLIED',7),(6,NULL,6,'APPLIED',8),(7,NULL,7,'ACCEPTED',9),(8,NULL,8,'APPLIED',10),(9,NULL,9,'APPLIED',11),(10,NULL,10,'ACCEPTED',12),(11,NULL,11,'APPLIED',13),(12,NULL,12,'APPLIED',14),(13,NULL,13,'ACCEPTED',15),(14,NULL,14,'APPLIED',16),(15,NULL,15,'APPLIED',17),(16,NULL,16,'ACCEPTED',18),(17,NULL,1,'APPLIED',19),(18,NULL,2,'APPLIED',20),(19,NULL,3,'ACCEPTED',21),(20,NULL,4,'APPLIED',22),(21,NULL,5,'APPLIED',23),(22,NULL,6,'ACCEPTED',24),(23,NULL,7,'APPLIED',25),(24,NULL,8,'APPLIED',26),(25,NULL,9,'ACCEPTED',27),(26,NULL,10,'APPLIED',28),(27,NULL,11,'APPLIED',29),(28,NULL,12,'ACCEPTED',30),(29,NULL,13,'APPLIED',31),(30,NULL,14,'APPLIED',32),(31,NULL,15,'ACCEPTED',33),(32,NULL,16,'APPLIED',34),(33,NULL,1,'APPLIED',35),(34,NULL,2,'ACCEPTED',36),(35,NULL,3,'APPLIED',37),(36,NULL,4,'APPLIED',38),(37,NULL,5,'ACCEPTED',39),(38,NULL,6,'APPLIED',40),(39,NULL,7,'APPLIED',41),(40,NULL,8,'ACCEPTED',42),(41,NULL,9,'APPLIED',43),(42,NULL,10,'APPLIED',44),(43,NULL,11,'ACCEPTED',45),(44,NULL,12,'APPLIED',46),(45,NULL,13,'APPLIED',47),(46,NULL,14,'ACCEPTED',48),(47,NULL,15,'APPLIED',49),(48,NULL,16,'APPLIED',50),(49,NULL,1,'ACCEPTED',51),(50,NULL,2,'APPLIED',52),(52,NULL,10,'APPLIED',3),(53,'2026-08-14 21:30:23.151378',6,'APPLIED',3);
/*!40000 ALTER TABLE `applications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jobs`
--

DROP TABLE IF EXISTS `jobs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jobs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `employer_id` bigint NOT NULL,
  `location` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jobs`
--

LOCK TABLES `jobs` WRITE;
/*!40000 ALTER TABLE `jobs` DISABLE KEYS */;
INSERT INTO `jobs` VALUES (1,NULL,'协助学院办公室日常事务，如文件整理、来电来访接待、会议服务等。',2,'行政楼 301','办公室助理'),(2,NULL,'负责实验室日常开放、设备登记与维护、安全巡查等。',2,'实验楼 B203','实验室管理员'),(3,NULL,'负责图书上架整理、借还登记、阅览区秩序维护等。',2,'图书馆一楼','图书管理员'),(4,NULL,'协助宿舍管理员做好入住登记、巡查与报修登记等。',2,'学生公寓管理处','宿舍协管员'),(5,NULL,'为来访人员提供校园讲解与路线引导服务。',2,'校史馆','校园导览员'),(6,NULL,'负责档案的分类、编号、装订与数字化扫描。',2,'档案馆','档案整理员'),(7,NULL,'负责机房开放值守、上机登记与设备巡检。',2,'计算中心','机房值班员'),(8,NULL,'负责多媒体教室设备开关、巡检与故障报修。',2,'教学楼 C','教室设备管理员'),(9,NULL,'负责体育器材借还登记、维护与场地管理。',2,'体育馆','体育器材管理员'),(10,NULL,'协助维持用餐高峰期排队秩序，引导文明就餐。',2,'学生食堂','食堂秩序维护员'),(11,NULL,'协助校园绿植养护与花坛维护工作。',2,'后勤管理处','校园绿化助理'),(12,NULL,'负责会议室预约登记、设备调试与卫生整理。',2,'行政楼 5 楼','会议室管理员'),(13,NULL,'协助校园网络的日常巡检与用户报修处理。',2,'信息中心','网络维护助理'),(14,NULL,'协助新生报到接待、引导与行李搬运等志愿服务。',2,'学生工作部','迎新志愿者'),(15,NULL,'协助心理咨询预约登记与来访接待。',2,'心理健康中心','心理咨询室助理'),(16,NULL,'协助校报素材收集、排版与校对工作。',2,'宣传部','校报编辑助理'),(17,NULL,'345',2,'234','123');
/*!40000 ALTER TABLE `jobs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `student_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schedule`
--

DROP TABLE IF EXISTS `schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `day_of_week` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `end_time` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `job_id` bigint NOT NULL,
  `start_time` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `student_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schedule`
--

LOCK TABLES `schedule` WRITE;
/*!40000 ALTER TABLE `schedule` DISABLE KEYS */;
INSERT INTO `schedule` VALUES (1,'周四','17:30',1,'14:00',3),(2,'周五','17:30',1,'14:00',3),(3,'周四','17:30',2,'14:00',4),(4,'周一','17:30',3,'14:00',5),(5,'周五','12:00',3,'08:00',5),(6,'周五','17:30',4,'14:00',6),(7,'周五','12:00',5,'08:00',7),(8,'周五','17:30',5,'14:00',7),(9,'周四','17:30',6,'14:00',8),(10,'周五','17:30',6,'14:00',8),(11,'周五','17:30',7,'14:00',9),(12,'周三','12:00',8,'08:00',10),(13,'周五','12:00',8,'08:00',10),(14,'周三','12:00',9,'08:00',11),(15,'周三','12:00',10,'08:00',12),(16,'周三','17:30',11,'14:00',13),(17,'周四','17:30',11,'14:00',13),(18,'周一','17:30',12,'14:00',14),(19,'周三','12:00',12,'08:00',14),(20,'周一','17:30',13,'14:00',15),(21,'周五','12:00',14,'08:00',16),(22,'周四','17:30',15,'14:00',17),(23,'周五','17:30',16,'14:00',18),(24,'周一','17:30',1,'14:00',19),(25,'周三','12:00',2,'08:00',20),(26,'周三','17:30',2,'14:00',20),(27,'周四','12:00',3,'08:00',21),(28,'周二','17:30',4,'14:00',22),(29,'周一','12:00',5,'08:00',23),(30,'周二','12:00',5,'08:00',23),(31,'周三','12:00',6,'08:00',24),(32,'周四','12:00',6,'08:00',24),(33,'周三','12:00',7,'08:00',25),(34,'周一','17:30',8,'14:00',26),(35,'周四','17:30',8,'14:00',26),(36,'周四','17:30',9,'14:00',27),(37,'周三','17:30',10,'14:00',28),(38,'周二','12:00',11,'08:00',29),(39,'周五','12:00',11,'08:00',29),(40,'周一','17:30',12,'14:00',30),(41,'周二','12:00',12,'08:00',30),(42,'周三','12:00',13,'08:00',31),(43,'周一','17:30',14,'14:00',32),(44,'周一','12:00',15,'08:00',33),(45,'周五','17:30',15,'14:00',33),(46,'周三','12:00',16,'08:00',34),(47,'周五','12:00',16,'08:00',34),(48,'周一','17:30',1,'14:00',35),(49,'周三','12:00',1,'08:00',35),(50,'周一','17:30',2,'14:00',36),(51,'周二','12:00',3,'08:00',37),(52,'周五','17:30',3,'14:00',37),(53,'周二','12:00',4,'08:00',38),(54,'周一','12:00',5,'08:00',39),(55,'周五','17:30',6,'14:00',40),(56,'周一','12:00',7,'08:00',41),(57,'周二','17:30',8,'14:00',42),(58,'周五','12:00',8,'08:00',42),(59,'周三','12:00',9,'08:00',43),(60,'周五','17:30',9,'14:00',43),(61,'周一','12:00',10,'08:00',44),(62,'周二','12:00',10,'08:00',44),(63,'周五','12:00',11,'08:00',45),(64,'周二','12:00',12,'08:00',46),(65,'周二','12:00',13,'08:00',47),(66,'周一','12:00',14,'08:00',48),(67,'周二','12:00',15,'08:00',49),(68,'周五','12:00',15,'08:00',49),(69,'周二','17:30',16,'14:00',50),(70,'周三','12:00',16,'08:00',50),(71,'周一','17:30',1,'14:00',51),(72,'周一','17:30',2,'14:00',52),(73,'周五','12:00',2,'08:00',52),(78,'周一','20:30',3,'15:30',3);
/*!40000 ALTER TABLE `schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `class_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `enabled` bit(1) DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `realname` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `username` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_r43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (2,NULL,NULL,NULL,'$2b$12$zJTHvFaEzLNA6Mq0XZUvT.t0lLyac94y86j447ZytfSgL6623lIjG','系统管理员','ADMIN','admin'),(3,'2026人工智能一班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','林慧鹏','STUDENT','student001'),(4,'2024计算机科学与技术一班',NULL,_binary '\0','$2a$10$0cPtr7tv2k/2HsucVpfPbOQNENXGaBhQniGRCKFVEt.TgNP6DwT6e','程伟勇','STUDENT','student002'),(5,'2025计算机科学与技术二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','段慧泽','STUDENT','student003'),(6,'2026计算机科学与技术二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','易琳艳','STUDENT','student004'),(7,'2025大数据管理与应用二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','吕敏燕','STUDENT','student005'),(8,'2026大数据管理与应用二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','姚涛秀','STUDENT','student006'),(9,'2026软件工程一班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','廖健敏','STUDENT','student007'),(10,'2025软件工程一班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','夏文平','STUDENT','student008'),(11,'2026大数据管理与应用二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','孔凤媛','STUDENT','student009'),(12,'2024大数据管理与应用二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','许芳静','STUDENT','student010'),(13,'2026大数据管理与应用二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','钱斌国','STUDENT','student011'),(14,'2025大数据管理与应用一班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','胡志飞','STUDENT','student012'),(15,'2024大数据管理与应用一班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','姚燕杰','STUDENT','student013'),(16,'2024软件工程一班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','王忠鹏','STUDENT','student014'),(17,'2024计算机科学与技术二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','许莹刚','STUDENT','student015'),(18,'2025人工智能二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','毛超鹏','STUDENT','student016'),(19,'2025软件工程一班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','吕鹏玲','STUDENT','student017'),(20,'2025计算机科学与技术一班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','阎洋强','STUDENT','student018'),(21,'2025大数据管理与应用一班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','田菁明','STUDENT','student019'),(22,'2026软件工程一班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','史琳建','STUDENT','student020'),(23,'2026人工智能二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','邵子丽','STUDENT','student021'),(24,'2026软件工程一班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','邵峰涵','STUDENT','student022'),(25,'2025计算机科学与技术一班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','金玉勇','STUDENT','student023'),(26,'2025大数据管理与应用一班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','姜辉玉','STUDENT','student024'),(27,'2024软件工程一班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','熊峰晨','STUDENT','student025'),(28,'2026软件工程一班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','邵丹玲','STUDENT','student026'),(29,'2024计算机科学与技术一班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','孔坤涵','STUDENT','student027'),(30,'2025软件工程一班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','胡怡杰','STUDENT','student028'),(31,'2026人工智能二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','谭慧晨','STUDENT','student029'),(32,'2025计算机科学与技术二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','袁军芳','STUDENT','student030'),(33,'2024人工智能二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','程娜媛','STUDENT','student031'),(34,'2026大数据管理与应用二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','潘慧涵','STUDENT','student032'),(35,'2024大数据管理与应用一班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','阎婷莹','STUDENT','student033'),(36,'2026人工智能二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','彭玉慧','STUDENT','student034'),(37,'2024计算机科学与技术二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','梁明静','STUDENT','student035'),(38,'2025软件工程二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','丁玉梅','STUDENT','student036'),(39,'2026大数据管理与应用二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','陆敏鹏','STUDENT','student037'),(40,'2025大数据管理与应用二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','张晨鹏','STUDENT','student038'),(41,'2026计算机科学与技术二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','孙子静','STUDENT','student039'),(42,'2024软件工程一班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','顾诺文','STUDENT','student040'),(43,'2025计算机科学与技术二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','傅琳欣','STUDENT','student041'),(44,'2024大数据管理与应用一班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','韩宇玲','STUDENT','student042'),(45,'2026计算机科学与技术一班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','徐雪燕','STUDENT','student043'),(46,'2025大数据管理与应用一班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','于媛丽','STUDENT','student044'),(47,'2026计算机科学与技术二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','常梅丹','STUDENT','student045'),(48,'2025计算机科学与技术一班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','曾婷鹏','STUDENT','student046'),(49,'2025软件工程一班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','秦武秀','STUDENT','student047'),(50,'2026人工智能二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','梁敏涛','STUDENT','student048'),(51,'2024人工智能二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','金刚诺','STUDENT','student049'),(52,'2026人工智能二班',NULL,NULL,'$2b$12$iROgpJsA8D6BLj3Y4knmAOanMvBUpEN2HuOchJKBqW2YSK9DX7Ha2','叶怡雪','STUDENT','student050');
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

-- Dump completed on 2026-08-15 14:36:32
