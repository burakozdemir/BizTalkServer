# Host: 127.0.0.1  (Version 5.5.5-10.1.36-MariaDB)
# Date: 2018-12-26 14:32:48
# Generator: MySQL-Front 6.1  (Build 1.26)


#
# Structure for table "jobs"
#

DROP TABLE IF EXISTS `jobs`;
CREATE TABLE `jobs` (
  `JobId` int(11) NOT NULL AUTO_INCREMENT,
  `JobOwner` int(11) NOT NULL DEFAULT '0',
  `Destination` text COLLATE utf8_turkish_ci NOT NULL,
  `FileUrl` text COLLATE utf8_turkish_ci NOT NULL,
  `Relatives` text COLLATE utf8_turkish_ci NOT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  `RuleId` int(11) NOT NULL DEFAULT '0',
  `InsertDateTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `UpdateDateTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `Description` text COLLATE utf8_turkish_ci NOT NULL,
  `OrchFlag` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`JobId`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_turkish_ci;


#
# Structure for table "orchestrations"
#

DROP TABLE IF EXISTS `orchestrations`;
CREATE TABLE `orchestrations` (
  `OrchestrationId` int(11) NOT NULL AUTO_INCREMENT,
  `OrchestrationOwner` int(11) NOT NULL DEFAULT '0',
  `Status` int(11) NOT NULL DEFAULT '0',
  `StartingJobId` int(11) NOT NULL DEFAULT '0',
  `InsertDateTime` datetime DEFAULT NULL,
  `UpdateDateTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`OrchestrationId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

#
# Data for table "orchestrations"
#


#
# Structure for table "rules"
#

DROP TABLE IF EXISTS `rules`;
CREATE TABLE `rules` (
  `RuleId` int(11) NOT NULL AUTO_INCREMENT,
  `RuleOwner` int(11) NOT NULL DEFAULT '0',
  `RuleQuery` text NOT NULL,
  `YesEdge` int(11) NOT NULL DEFAULT '0',
  `NoEdge` int(11) NOT NULL DEFAULT '0',
  `RelativeResult` text NOT NULL,
  PRIMARY KEY (`RuleId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

#
# Data for table "rules"
#

