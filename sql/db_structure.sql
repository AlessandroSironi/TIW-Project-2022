DROP TABLE IF EXISTS Invitation;
DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Meeting;

CREATE TABLE User (
    ID INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    mail VARCHAR(100) NOT NULL,
    user VARCHAR(100) NOT NULL,
    psw_hash VARBINARY(128) NOT NULL,
    psw_salt VARBINARY(16) NOT NULL,
    name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL
);

CREATE TABLE Meeting (
    ID INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    ID_Creator INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    startDate DATETIME NOT NULL,
    duration INT NOT NULL,
    capacity INT NOT NULL
);

CREATE TABLE Invitation (
    ID INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    IDMeeting INT NOT NULL,
    IDUser INT NOT NULL,

    FOREIGN KEY (IDMeeting) REFERENCES Meeting(ID)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (IDUser) REFERENCES User(ID)
        ON UPDATE CASCADE
        ON DELETE CASCADE
)