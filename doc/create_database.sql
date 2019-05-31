CREATE DATABASE IF NOT EXISTS `elephant` DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;
USE `elephant`;

CREATE TABLE `video` (
  `viewkey` VARCHAR(64) NOT NULL PRIMARY KEY comment 'viewkey',
  `title` VARCHAR(128) NOT NULL comment '标题',
  `runtime` VARCHAR(16) NOT NULL comment '时长',
  `added` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' comment '添加时间',
  `from` VARCHAR(64) NOT NULL comment '作者',
  `views` INT NOT NULL comment '查看',
  `favorites` INT NOT NULL comment '收藏',
  `comments` INT NOT NULL comment '留言',
  `point` INT NOT NULL comment '积分',
  `status` INT NOT NULL DEFAULT 0 comment '视频状态 0存在 1不存在',
  `download` INT NOT NULL DEFAULT 0 comment '下载状态 0未下载 1已下载',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '更新时间',
  INDEX `status_download_views` (`status`,`download`,`views`),
  INDEX `status_download_favorites` (`status`,`download`,`favorites`),
  INDEX `status_download_comments` (`status`,`download`,`comments`),
  INDEX `status_download_point` (`status`,`download`,`point`),
  INDEX `status_download_create_time` (`status`,`download`,`create_time`)
) ENGINE InnoDB DEFAULT CHARSET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;
