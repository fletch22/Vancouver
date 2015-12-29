select 1 = 1;

CREATE TABLE `orblog`.`undoactionlog` (
  `undoAction` longtext NOT NULL,
  `tranDate` decimal(30,12) NOT NULL DEFAULT '0.000000000000',
  `tranId` decimal(30,12) NOT NULL DEFAULT '0.000000000000',
  PRIMARY KEY (`tranDate`),
  KEY `undoAction_tranId` (`tranId`),
  KEY `tranId` (`tranId`)
);

CREATE TABLE `orblog`.`test` (
  `col1` decimal(40,15) DEFAULT NULL
);

CREATE TABLE `orblog`.`currenttransaction` (
  `tranId` decimal(30,12) NOT NULL DEFAULT '0.000000000000'
);

CREATE TABLE `orblog`.`actionlog` (
  `action` longtext NOT NULL,
  `tranDate` decimal(30,12) NOT NULL DEFAULT '0.000000000000',
  PRIMARY KEY (`tranDate`)
);