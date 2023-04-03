DROP TABLE IF EXISTS `t_live_comment`;
CREATE TABLE `t_live_comment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `principal_id` varchar(100) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `comment` varchar(200) DEFAULT NULL,
  `gift_id` varchar(100) DEFAULT NULL,
  `combo_count` int DEFAULT NULL,
  `fans_rank` int DEFAULT NULL,
  `c_type` smallint DEFAULT NULL,
  `create_time` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `t_live_comment_gen_time_IDX` (`create_time`) USING BTREE,
  KEY `t_live_comment_principal_id_IDX` (`principal_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `t_live_kda`;
CREATE TABLE `t_live_kda` (
  `id` int NOT NULL AUTO_INCREMENT,
  `principal_id` varchar(100) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `comment` varchar(200) DEFAULT NULL,
  `kill_count` int DEFAULT NULL,
  `death` int DEFAULT NULL,
  `assist` int DEFAULT NULL,
  `player` varchar(5) DEFAULT NULL,
  `player_role` varchar(5) DEFAULT NULL,
  `game_result` char(1) DEFAULT NULL,
  `game_id` int DEFAULT NULL,
  `status` TINYINT DEFAULT 0,
  `winner` TINYINT DEFAULT 0,
  `create_time` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `t_live_kda_gen_time_IDX` (`game_id`, `create_time`) USING BTREE,
  KEY `t_live_kda_round_IDX` (`principal_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `t_live_user`;
CREATE TABLE `t_live_user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `principal_id` varchar(100) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `intimacy_level` int DEFAULT NULL,
  `fans_group_intimacy_level` int DEFAULT NULL,
  `wealth_grade` int DEFAULT NULL,
  `badge_key` varchar(100) DEFAULT NULL,
  `contact_id` varchar(100) DEFAULT NULL,
  `contact_type` varchar(20) DEFAULT NULL,
  `create_time` bigint DEFAULT NULL,
  `update_time` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `t_live_user_principal_id_IDX` (`principal_id`),
  KEY `t_live_user_name` (`name`),
  UNIQUE KEY(principal_id),
  KEY `t_live_user_create_time_IDX` (`update_time`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `t_live_watching`;
CREATE TABLE `t_live_watching` (
  `id` int NOT NULL AUTO_INCREMENT,
  `watching` int DEFAULT NULL,
  `likes` int DEFAULT NULL,
  `create_time` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `t_live_watching_gen_time_IDX` (`create_time`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `t_live_game`;
CREATE TABLE `t_live_game` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_count` int DEFAULT NULL,
  `kill_count` int DEFAULT NULL,
  `death` int DEFAULT NULL,
  `assist` int DEFAULT NULL,
  `player` varchar(20) DEFAULT NULL,
  `game_result` char(1) DEFAULT NULL,
  `play_mode` varchar(10) DEFAULT NULL,
  `bonus_mode` varchar(10) DEFAULT NULL,
  `bonus_amount` int DEFAULT NULL,
  `create_time` bigint DEFAULT NULL,
  `update_time` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `t_live_game_create_time_IDX` (`create_time`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `t_live_challenge`;
CREATE TABLE `t_live_challenge` (
  `id` int NOT NULL AUTO_INCREMENT,
  `game_id` int NULL,
  `kda_id` int NULL,
  `principal_id` varchar(100) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `create_time` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `t_live_challenge_create_time_IDX` (`create_time`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `t_live_hero`;
CREATE TABLE `t_live_hero` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL,
  `alias` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `t_live_role`;
CREATE TABLE `t_live_role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `t_live_hero_role`;
CREATE TABLE `t_live_hero_role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `hero_id` int NOT NULL,
  `role_id` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;