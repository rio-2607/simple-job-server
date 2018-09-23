CREATE TABLE `simple_job_lock` (
  `ID` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `JobName` varchar(200) NOT NULL COMMENT 'JOB_NAME',
  `JobHash` int(11) unsigned NOT NULL COMMENT 'hash16',
  `Status` varchar(8) NOT NULL COMMENT 'Job状态',
  `AddTime` datetime NOT NULL COMMENT '任务添加时间',
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '任务更新时间',
  `host` varchar(100) DEFAULT NULL COMMENT '加锁的机器的IP',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_JobHash` (`JobHash`),
  KEY `IX_UpdateTime` (`UpdateTime`)
) ENGINE=InnoDB AUTO_INCREMENT=4442845 DEFAULT CHARSET=utf8 COMMENT='任务锁';


CREATE TABLE `simple_job_lock_job_log` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `JobName` varchar(256) NOT NULL COMMENT 'job名称',
  `StartedTime` datetime NOT NULL COMMENT 'job触发时间',
  `FinishedTime` datetime DEFAULT NULL COMMENT 'job执行完成时间',
  `host` varchar(100) DEFAULT NULL COMMENT '执行job机器',
  `AddTime` datetime NOT NULL COMMENT '添加时间',
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `Message` varchar(1000) DEFAULT '',
  PRIMARY KEY (`ID`),
  KEY `IDX_JOBNAME` (`JobName`(255)),
  KEY `IDX_STARTEDTIME` (`StartedTime`),
  KEY `IX_UpdateTime` (`UpdateTime`)
) ENGINE=InnoDB AUTO_INm CREMENT=9511 DEFAULT CHARSET=utf8 COMMENT='任务日志';


CREATE TABLE `simple_job_lock_job_switch` (
  `ID` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `JobName` varchar(200) NOT NULL COMMENT 'JOB_NAME',
  `Status` int(1) NOT NULL COMMENT 'Status',
  `AddTime` datetime NOT NULL COMMENT '记录添加时间',
  `UpdateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_JOBNAME` (`JobName`),
  KEY `IX_updatetime` (`UpdateTime`)
) ENGINE=InnoDB AUTO_INCREMENT=198 DEFAULT CHARSET=utf8 COMMENT='任务开关';