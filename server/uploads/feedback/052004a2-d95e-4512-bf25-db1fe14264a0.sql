-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: pet_foster_system
-- ------------------------------------------------------
-- Server version	8.0.41

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
-- Table structure for table `booking_order`
--
create database if not exists `pet_foster_system` default character set utf8mb4 collate utf8mb4_general_ci;
use `pet_foster_system`;
DROP TABLE IF EXISTS `booking_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `booking_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `owner_id` bigint NOT NULL,
  `pet_id` bigint NOT NULL,
  `station_id` bigint NOT NULL,
  `package_id` bigint DEFAULT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_general_ci DEFAULT 'SUBMITTED' COMMENT 'SUBMITTED/CHECKED_IN/IN_CARE/SETTLED/CANCELLED',
  `checkin_note` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `checkout_note` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no` (`order_no`),
  KEY `fk_order_owner` (`owner_id`),
  KEY `fk_order_pet` (`pet_id`),
  KEY `fk_order_station` (`station_id`),
  KEY `fk_order_package` (`package_id`),
  CONSTRAINT `fk_order_owner` FOREIGN KEY (`owner_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `fk_order_package` FOREIGN KEY (`package_id`) REFERENCES `foster_package` (`id`),
  CONSTRAINT `fk_order_pet` FOREIGN KEY (`pet_id`) REFERENCES `pet_profile` (`id`),
  CONSTRAINT `fk_order_station` FOREIGN KEY (`station_id`) REFERENCES `foster_station` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `booking_order`
--

LOCK TABLES `booking_order` WRITE;
/*!40000 ALTER TABLE `booking_order` DISABLE KEYS */;
INSERT INTO `booking_order` VALUES (1,'ORD9B72E3B2',4,1,1,2,'2026-04-05','2026-04-05',200.00,'SETTLED',NULL,NULL,'2026-04-05 16:57:52'),(2,'ORDB04F934C',4,1,1,1,'2026-04-05','2026-04-05',400.00,'IN_CARE',NULL,NULL,'2026-04-05 18:14:37'),(3,'ORD90E1140F',5,2,1,3,'2026-04-05','2026-04-05',100.00,'SETTLED',NULL,NULL,'2026-04-05 18:38:23');
/*!40000 ALTER TABLE `booking_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_message`
--

DROP TABLE IF EXISTS `chat_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sender_id` bigint DEFAULT NULL COMMENT '发送人ID',
  `receiver_id` bigint DEFAULT NULL COMMENT '接收人ID',
  `station_id` bigint DEFAULT NULL COMMENT '关联站点ID',
  `content` text COLLATE utf8mb4_general_ci COMMENT '消息内容',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `is_read` int DEFAULT '0' COMMENT '是否已读(0-未读, 1-已读)',
  PRIMARY KEY (`id`),
  KEY `idx_station_user` (`station_id`,`sender_id`,`receiver_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_message`
--

LOCK TABLES `chat_message` WRITE;
/*!40000 ALTER TABLE `chat_message` DISABLE KEYS */;
INSERT INTO `chat_message` VALUES (1,4,2,1,'请问现在还有位置吗？','2026-04-05 18:13:26',0),(2,2,4,1,'有的','2026-04-05 18:13:37',0),(3,5,2,1,'请问现在有空位吗？','2026-04-05 18:37:57',0),(4,2,5,1,'有的','2026-04-05 18:38:07',0);
/*!40000 ALTER TABLE `chat_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `foster_package`
--

DROP TABLE IF EXISTS `foster_package`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `foster_package` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `station_id` bigint NOT NULL,
  `package_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `pet_type` varchar(30) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `days` int NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `has_daily_video` tinyint(1) DEFAULT '0',
  `description` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_general_ci DEFAULT 'ON',
  PRIMARY KEY (`id`),
  KEY `fk_package_station` (`station_id`),
  CONSTRAINT `fk_package_station` FOREIGN KEY (`station_id`) REFERENCES `foster_station` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `foster_package`
--

LOCK TABLES `foster_package` WRITE;
/*!40000 ALTER TABLE `foster_package` DISABLE KEYS */;
INSERT INTO `foster_package` VALUES (1,1,'宠物洗澡','狗狗',1,400.00,0,NULL,'ON'),(2,1,'猫猫寄养看护一天','猫猫',1,200.00,0,'全天照顾','ON'),(3,1,'狗狗照看一天','狗狗',1,100.00,0,'','ON');
/*!40000 ALTER TABLE `foster_package` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `foster_station`
--

DROP TABLE IF EXISTS `foster_station`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `foster_station` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `address` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `contact_person` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `contact_phone` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `business_hours` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `qualification_status` varchar(20) COLLATE utf8mb4_general_ci DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED',
  `price_standard` decimal(10,2) DEFAULT '0.00',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `image_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `foster_station`
--

LOCK TABLES `foster_station` WRITE;
/*!40000 ALTER TABLE `foster_station` DISABLE KEYS */;
INSERT INTO `foster_station` VALUES (1,'萌宠之家旗舰店','朝阳区幸福路1号','张店长','13900000001','09:00-21:00','APPROVED',120.00,'2026-04-04 16:40:50','http://localhost:8080/uploads/13a778bd-8175-401d-92ad-d6fd79a6a079_微信图片_20250601203658.jpg'),(2,'喵汪寄养中心','海淀区科创路88号','李店长','13900000002','08:30-20:30','APPROVED',150.00,'2026-04-04 16:40:50',NULL),(4,'狗狗乐园','上海市','张三','1811111111111','09:00 - 21:00','APPROVED',100.00,'2026-04-05 19:56:32',NULL);
/*!40000 ALTER TABLE `foster_station` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pet_health_record`
--

DROP TABLE IF EXISTS `pet_health_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pet_health_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `booking_id` bigint NOT NULL,
  `record_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `body_state` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `feed_note` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `activity_note` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `media_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_health_booking` (`booking_id`),
  CONSTRAINT `fk_health_booking` FOREIGN KEY (`booking_id`) REFERENCES `booking_order` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pet_health_record`
--

LOCK TABLES `pet_health_record` WRITE;
/*!40000 ALTER TABLE `pet_health_record` DISABLE KEYS */;
INSERT INTO `pet_health_record` VALUES (1,1,'2026-04-05 17:09:36','','',NULL,''),(2,1,'2026-04-05 17:59:52','很不错','一天三餐',NULL,'http://localhost:8080/uploads/2eae6cc0-a595-45b3-9bff-c868e3b18d66_2026-03-22 17-32-41.mp4'),(3,1,'2026-04-05 18:00:55','很不错','一天三餐',NULL,'http://localhost:8080/uploads/ae195a23-76f3-4113-aad0-773ebd194250_md.webp'),(4,2,'2026-04-05 18:15:20','很有精神','一天三餐',NULL,'http://localhost:8080/uploads/99771df6-159a-4c08-95bc-5c508b0ffb74_1.jpg'),(5,2,'2026-04-05 18:15:39','很有精神','一天三餐',NULL,''),(6,2,'2026-04-05 18:16:03','很有精神','一天三餐',NULL,''),(7,2,'2026-04-05 18:16:53','很有精神','一天三餐',NULL,''),(8,2,'2026-04-05 18:19:35','不错','三餐',NULL,'http://localhost:8080/uploads/9f045a60-242f-491c-9919-ad101572666c_2026-03-22 17-32-41.mp4'),(9,3,'2026-04-05 18:39:39','不错','正常',NULL,''),(10,3,'2026-04-05 18:41:11','不错','正常',NULL,'http://localhost:8080/uploads/71fb1c02-db10-4edb-a280-e47b133900a8_cq.webp'),(11,3,'2026-04-05 18:42:42','身体不错','正常',NULL,'http://localhost:8080/uploads/74b2e18c-e28d-43a8-882c-11a210d9bdfa_cq.webp'),(12,3,'2026-04-05 18:43:38','身体不错','正常',NULL,'http://localhost:8080/uploads/498ec699-c36a-4952-94ea-2098f57138d3_2026-03-22 17-32-41.mp4'),(13,2,'2026-04-05 19:57:26','123','123',NULL,'http://localhost:8080/uploads/f798d0cb-9e0f-4354-8710-32be5aad3628_新对话.png'),(14,2,'2026-04-05 19:57:44','非常好','正常',NULL,'');
/*!40000 ALTER TABLE `pet_health_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pet_profile`
--

DROP TABLE IF EXISTS `pet_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pet_profile` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `owner_id` bigint NOT NULL,
  `pet_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `pet_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `breed` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `age` int DEFAULT NULL,
  `gender` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `vaccine_record` text COLLATE utf8mb4_general_ci,
  `medical_history` text COLLATE utf8mb4_general_ci,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_pet_owner` (`owner_id`),
  CONSTRAINT `fk_pet_owner` FOREIGN KEY (`owner_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pet_profile`
--

LOCK TABLES `pet_profile` WRITE;
/*!40000 ALTER TABLE `pet_profile` DISABLE KEYS */;
INSERT INTO `pet_profile` VALUES (1,4,'咪咪','猫猫','橘猫',1,'公','3针','没有过敏史','2026-04-05 16:57:34'),(2,5,'旺财','狗狗','中国土狗',2,'公','三针','没有过敏史','2026-04-05 18:35:52');
/*!40000 ALTER TABLE `pet_profile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `booking_id` bigint DEFAULT NULL,
  `owner_id` bigint DEFAULT NULL,
  `owner_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `station_id` bigint DEFAULT NULL,
  `content` text COLLATE utf8mb4_general_ci,
  `rating` double DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `supply_inventory`
--

DROP TABLE IF EXISTS `supply_inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `supply_inventory` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `station_id` bigint NOT NULL,
  `item_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `quantity` int DEFAULT '0',
  `warning_threshold` int DEFAULT '10',
  `low_stock_warning` tinyint(1) DEFAULT '0',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_supply_station` (`station_id`),
  CONSTRAINT `fk_supply_station` FOREIGN KEY (`station_id`) REFERENCES `foster_station` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `supply_inventory`
--

LOCK TABLES `supply_inventory` WRITE;
/*!40000 ALTER TABLE `supply_inventory` DISABLE KEYS */;
INSERT INTO `supply_inventory` VALUES (1,1,'猫砂',10,5,0,'2026-04-05 18:50:20');
/*!40000 ALTER TABLE `supply_inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `role` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'SYSTEM_ADMIN/STATION_ADMIN/OWNER',
  `real_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `phone` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `id_card` varchar(30) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `certified` tinyint(1) DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `station_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','123456','SYSTEM_ADMIN','系统管理员','13800000000',NULL,1,'2026-04-04 16:40:50','2026-04-04 16:40:50',NULL),(2,'station1','123456','STATION_ADMIN','站点管理员A','13800000001',NULL,1,'2026-04-04 16:40:50','2026-04-04 16:40:50',NULL),(3,'owner1','123456','OWNER','宠物主人A','13800000002',NULL,1,'2026-04-04 16:40:50','2026-04-04 16:40:50',NULL),(4,'test1','123456','OWNER',NULL,NULL,NULL,1,'2026-04-05 16:23:29','2026-04-05 16:23:29',NULL),(5,'test2','123456','OWNER',NULL,NULL,NULL,1,'2026-04-05 18:35:16','2026-04-05 18:35:16',NULL),(6,'zhangsan','123456','STATION_ADMIN','张三','1811111111111',NULL,0,'2026-04-05 19:56:32','2026-04-05 19:56:31',4);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-06  0:32:19
