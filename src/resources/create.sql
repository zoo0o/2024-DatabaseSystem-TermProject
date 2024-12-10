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
                      cid INT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(100) NOT NULL,
                      is_academic BOOLEAN NOT NULL,
                      location VARCHAR(255),
                      president_sid INT  NOT NULL,
                      advisor_pid INT NOT NULL,
                      FOREIGN KEY (president_sid) REFERENCES student(sid),
                      FOREIGN KEY (advisor_pid) REFERENCES professor(pid)
);

CREATE TABLE clubmember (
                            sid INT,
                            cid INT,
                            join_date DATE NOT NULL,
                            PRIMARY KEY (sid, cid),
                            FOREIGN KEY (sid) REFERENCES student(sid),
                            FOREIGN KEY (cid) REFERENCES club(cid)
);

CREATE TABLE document (
                          did INT AUTO_INCREMENT PRIMARY KEY,
                          title VARCHAR(255) NOT NULL,
                          type VARCHAR(50) NOT NULL,
                          content TEXT,
                          submit_cid VARCHAR(20) NOT NULL,
                          submit_date DATE NOT NULL,
                          approve_aid VARCHAR(20),
                          approve_date DATE,
                          is_approved BOOLEAN,
                          FOREIGN KEY (submit_cid) REFERENCES club(cid),
                          FOREIGN KEY (approve_aid) REFERENCES assistant(aid)
);