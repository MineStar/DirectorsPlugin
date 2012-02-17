CREATE TABLE IF NOT EXISTS `directorblockdata` (
    `Id` int(11) NOT NULL AUTO_INCREMENT,
    `WorldName` varchar(255) DEFAULT NULL,
    `BlockX` int(11) DEFAULT NULL,
    `BlockY` int(11) DEFAULT NULL,
    `BlockZ` int(11) DEFAULT NULL,
    `NewBlockId` int(11) DEFAULT NULL,
    `NewBlockData` int(11) DEFAULT NULL,
    `OldBlockId` int(11) DEFAULT NULL,
    `OldBlockData` int(11) DEFAULT NULL,
    `DateTime` datetime DEFAULT NULL,
    `PlayerName` varchar(255) DEFAULT NULL,
    `EventType` char(1) DEFAULT NULL,
    `AreaName` varchar(255) DEFAULT NULL,
      PRIMARY KEY (`Id`)
    ) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;

CREATE TABLE IF NOT EXISTS `directorareadata` (
    `Id` int(11) NOT NULL AUTO_INCREMENT,
    `AreaName` varchar(255) DEFAULT NULL,
    `AreaWorld` varchar(255) DEFAULT NULL,
    `Chunk1X` int(11) DEFAULT NULL,
    `Chunk1Z` int(11) DEFAULT NULL,
    `Chunk2X` int(11) DEFAULT NULL,
    `Chunk2Z` int(11) DEFAULT NULL,
    `AreaOwner` varchar(255) DEFAULT NULL,
     PRIMARY KEY (`Id`)
    ) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;