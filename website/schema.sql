DROP TABLE IF EXISTS `groups`;

#
# Table structure for table 'groups'
#

CREATE TABLE `groups` (
  `id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `description` varchar(100) NOT NULL,
  `permissions` text DEFAULT NULL,
  `deleted` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
);

#
# Dumping data for table 'groups'
#

INSERT INTO `groups` (`id`, `name`, `description`, `permissions`, `deleted`)
VALUES
  (1, 'admin', 'Administrator', '{\"auth\":\"1\",\"backup\":\"1\",\"website\":\"1\",\"dashboard\":\"1\",\"groups\":\"1\",\"options\":\"1\",\"permissions\":\"1\",\"users\":\"1\"}', 0),
  (2, 'members', 'General User', '{\"auth\":\"1\",\"website\":\"1\"}', 0),
  (3, 'guest', 'Guest User', '{\"auth\":\"1\",\"website\":\"1\"}', 0);

DROP TABLE IF EXISTS `meta`;

#
# Table structure for table 'meta'
#

CREATE TABLE `meta` (
  `id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` mediumint(8) unsigned DEFAULT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `company` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
);


#
# Dumping data for table 'meta'
#

INSERT INTO `meta` (`id`, `user_id`, `first_name`, `last_name`, `company`, `phone`) VALUES
	('1','1','Admin','istrator','ADMIN','0');

DROP TABLE IF EXISTS `users`;

#
# Table structure for table 'users'
#

CREATE TABLE `users` (
  `id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `group_id` mediumint(8) unsigned NOT NULL,
  `ip_address` char(16) NOT NULL,
  `username` varchar(15) NOT NULL,
  `password` varchar(40) NOT NULL,
  `salt` varchar(40) DEFAULT NULL,
  `email` varchar(254) NOT NULL,
  `activation_code` varchar(40) DEFAULT NULL,
  `forgotten_password_code` varchar(40) DEFAULT NULL,
  `remember_code` varchar(40) DEFAULT NULL,
  `created_on` int(11) unsigned NOT NULL,
  `last_login` int(11) unsigned DEFAULT NULL,
  `active` tinyint(1) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
);

#
# Dumping data for table 'users'
#

INSERT INTO `users` (`id`, `group_id`, `ip_address`, `username`, `password`, `salt`, `email`, `activation_code`, `forgotten_password_code`, `created_on`, `last_login`, `active`) VALUES
	('1','1','127.0.0.1','administrator','59beecdf7fc966e2f17fd8f65a4a9aeb09d4a3d4','9462e8eee0','admin@admin.com','',NULL,'1268889823','1268889823','1');

DROP TABLE IF EXISTS `settings`;

#
# Table structure for table 'settings'
#

CREATE TABLE `settings` (
  `option_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `option_name` varchar(64) NOT NULL DEFAULT '',
  `option_value` mediumtext NOT NULL,
  `option_group` varchar(55) NOT NULL DEFAULT 'site',
  `auto_load` enum('no','yes') NOT NULL DEFAULT 'yes',
  PRIMARY KEY (`option_id`,`option_name`),
  KEY `option_name` (`option_name`),
  KEY `auto_load` (`auto_load`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

#
# Dumping data for table 'settings'
#

INSERT INTO `settings` (`option_name`, `option_value`, `option_group`, `auto_load`) VALUES
  ('site_name', 'SITE NAME', 'site', 'yes');

DROP TABLE IF EXISTS `history`;

#
# Table structure for table 'history'
#

CREATE TABLE `history` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `table_name` varchar(255) DEFAULT NULL,
  `action` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `updated_at` int(11) DEFAULT NULL,
  `row_id` int(11) DEFAULT NULL,
  `row_data` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;