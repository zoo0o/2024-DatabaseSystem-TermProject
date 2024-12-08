CREATE DATABASE IF NOT EXISTS clubdb;
USE clubdb;

CREATE TABLE student (
                         sid INT PRIMARY KEY,
                         pwd VARCHAR(255) NOT NULL,
                         name VARCHAR(100) NOT NULL,
                         department VARCHAR(100),
                         status VARCHAR(50),
                         phone VARCHAR(20)
);

CREATE TABLE professor (
                           pid INT PRIMARY KEY,
                           pwd VARCHAR(255) NOT NULL,
                           name VARCHAR(100) NOT NULL,
                           department VARCHAR(100),
                           phone VARCHAR(20)
);

CREATE TABLE club (
                      cid INT PRIMARY KEY,
                      name VARCHAR(100) NOT NULL,
                      is_academic BOOLEAN NOT NULL,
                      location VARCHAR(255),
                      mgr_pid INT,
                      FOREIGN KEY (mgr_pid) REFERENCES professor(pid)
);

CREATE TABLE clubmember (
                            sid INT,
                            cid INT,
                            join_date DATE,
                            role VARCHAR(50) DEFAULT 'member',
                            PRIMARY KEY (sid, cid),
                            FOREIGN KEY (sid) REFERENCES student(sid),
                            FOREIGN KEY (cid) REFERENCES club(cid)
);

CREATE TABLE document (
                          did INT AUTO_INCREMENT PRIMARY KEY,
                          title VARCHAR(255) NOT NULL,
                          type VARCHAR(50),
                          content TEXT
);

CREATE TABLE assistant (
                           aid INT PRIMARY KEY,
                           pwd VARCHAR(255) NOT NULL,
                           name VARCHAR(100) NOT NULL,
                           department VARCHAR(100),
                           phone VARCHAR(20)
);

CREATE TABLE submit (
                        did INT,
                        cid INT,
                        submit_date DATE,
                        PRIMARY KEY (did, cid),
                        FOREIGN KEY (did) REFERENCES document(did),
                        FOREIGN KEY (cid) REFERENCES club(cid)
);
CREATE TABLE approve (
                         did INT,
                         aid INT,
                         approve_date DATE,
                         is_approved BOOLEAN NOT NULL,
                         PRIMARY KEY (did, aid),
                         FOREIGN KEY (did) REFERENCES document(did),
                         FOREIGN KEY (aid) REFERENCES assistant(aid)
);
