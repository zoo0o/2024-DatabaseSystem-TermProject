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

CREATE TABLE assistant (
                           aid INT PRIMARY KEY,
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
                      president_sid INT NOT NULL,
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
                          submit_cid INT NOT NULL,
                          submit_date DATETIME NOT NULL,
                          approve_aid INT,
                          approve_date DATETIME,
                          is_approved BOOLEAN,
                          FOREIGN KEY (submit_cid) REFERENCES club(cid),
                          FOREIGN KEY (approve_aid) REFERENCES assistant(aid)
);

INSERT INTO student (sid, pwd, name, department, status, phone) VALUES
                                                                    (2022078020, 'password1', '김지유', '소프트웨어학부', '재학', '010-0000-0000'),
                                                                    (2020039010, 'password2', '홍길동', '소프트웨어학과', '재학', '010-0001-0001'),
                                                                    (2021050010, 'password3', '박수현', '소프트웨어학부', '재학', '010-0002-0002'),
                                                                    (2021089011, 'password4', '이정우', '소프트웨어학과', '재학', '010-0003-0003'),
                                                                    (2021092010, 'password5', '김민지', '소프트웨어학부', '재학', '010-0004-0004'),
                                                                    (2022011020, 'password6', '최지훈', '소프트웨어학부', '재학', '010-0005-0005'),
                                                                    (2021075020, 'password7', '박지민', '소프트웨어학부', '재학', '010-0006-0006'),
                                                                    (2020038011, 'password8', '오유정', '소프트웨어학과', '재학', '010-0007-0007'),
                                                                    (2022078012, 'password9', '정수현', '컴퓨터공학과', '재학', '010-0008-0008'),
                                                                    (2020089010, 'password10', '김하윤', '전자공학과', '재학', '010-0009-0009');

INSERT INTO professor (pid, pwd, name, department, phone) VALUES
                                                              (101, 'profpass1', '노서영', '소프트웨어학부', '010-1111-2222'),
                                                              (102, 'profpass2', '홍장의', '소프트웨어학부', '010-1112-2222'),
                                                              (103, 'profpass3', '이건명', '소프트웨어학부', '010-1113-2223'),
                                                              (104, 'profpass4', '이의종', '소프트웨어학부', '010-1114-2224'),
                                                              (105, 'profpass5', '이재성', '소프트웨어학부', '010-1115-2225'),
                                                              (106, 'profpass6', '조오현', '소프트웨어학부', '010-1116-2226'),
                                                              (107, 'profpass7', '이종연', '소프트웨어학부', '010-1117-2227'),
                                                              (108, 'profpass8', '최경주', '소프트웨어학부', '010-1118-2228'),
                                                              (109, 'profpass9', '정지훈', '소프트웨어학부', '010-1119-2229'),
                                                              (110, 'profpass10', '강윤석', '소프트웨어학부', '010-1120-2230');

INSERT INTO assistant (aid, pwd, name, department, phone) VALUES
                                                              (39101, 'asspass1', '김민재', '소프트웨어학부', '010-7777-8888'),
                                                              (39102, 'asspass2', '이지은', '소프트웨어학부', '010-7778-8889'),
                                                              (39103, 'asspass3', '박재훈', '소프트웨어학부', '010-7779-8890');

INSERT INTO club (cid, name, is_academic, location, president_sid, advisor_pid) VALUES
                                                                                    (1, '네스트넷', TRUE, 'S4-1 111호', 2022078020, 101),
                                                                                    (2, 'CUVIC', TRUE, 'S4-1 112호', 2020039010, 102),
                                                                                    (3, 'PDA', TRUE, 'S4-1 113호', 2021050010, 103),
                                                                                    (4, 'NOVA', TRUE, 'S4-1 114호', 2021089011, 104),
                                                                                    (5, 'TUX', TRUE, 'S4-1 115호', 2021092010, 105),
                                                                                    (6, '샘마루', TRUE, 'S4-1 116호', 2022011020, 106),
                                                                                    (7, '엠시스', TRUE, 'S4-1 117호', 2021075020, 107),
                                                                                    (8, '빈즈', FALSE, 'ㅁㅁ음악실', 2020038011, 108),
                                                                                    (9, '축구', FALSE, '대운동장', 2022078012, 109),
                                                                                    (10, '농구', FALSE, 'S4-1 앞 농구장', 2020089010, 110);


INSERT INTO clubmember (sid, cid, join_date) VALUES
                                                 (2022078020, 1, '2024-01-15'),
                                                 (2020039010, 2, '2024-01-20'),
                                                 (2021050010, 3, '2024-01-25'),
                                                 (2021089011, 4, '2024-02-10'),
                                                 (2021092010, 5, '2024-02-15'),
                                                 (2022011020, 6, '2024-02-20'),
                                                 (2021075020, 7, '2024-03-01'),
                                                 (2020038011, 8, '2024-03-05'),
                                                 (2022078012, 9, '2024-03-10'),
                                                 (2020089010, 10, '2024-03-15'),
                                                 (2020038011, 1, '2024-04-05'),
                                                 (2022078012, 1, '2024-04-10'),
                                                 (2020089010, 1, '2024-04-15'),
                                                 (2022078020, 8, '2024-04-15'),
                                                 (2022078020, 9, '2024-05-05'),
                                                 (2020039010, 8, '2024-05-15');

INSERT INTO document (did, title, type, content, submit_cid, submit_date, approve_aid, approve_date, is_approved) VALUES
                                                                                                                      (1, '알고리즘 스터디 계획', '계획서', '알고리즘 스터디 방향과 목표에 대한 계획입니다.', 1, '2024-01-20 10:15:00', 39101, '2024-01-25 14:30:00', TRUE),
                                                                                                                      (2, '네스트넷 회의록', '회의록', '네스트넷의 첫 번째 정기 회의 내용입니다.', 1, '2024-01-22 11:00:00', 39101, '2024-01-30 16:45:00', TRUE),
                                                                                                                      (3, 'PDA 회원 명단', '명단', 'PDA의 회원 명단입니다.', 3, '2024-01-25 09:30:00', 39103, '2024-01-28 10:00:00', TRUE),
                                                                                                                      (4, '네스트넷 프로젝트 계획', '계획서', '네스트넷의 2024년 프로젝트 계획입니다.', 1, '2024-02-10 14:00:00', NULL, NULL, FALSE),
                                                                                                                      (5, '네스트넷 회의록', '회의록', '네스트넷의 두 번째 정기 회의 내용입니다.', 1, '2024-02-15 15:20:00', 39101, '2024-02-20 18:00:00', TRUE),
                                                                                                                      (6, '네스트넷 회원 명단', '명단', '네스트넷의 회원 명단입니다.', 1, '2024-02-20 09:15:00', 39102, '2024-02-25 12:45:00', TRUE),
                                                                                                                      (7, '엠시스 프로젝트 보고서', '계획서', '엠시스의 프로젝트 진행 보고 내용입니다.', 7, '2024-03-01 13:00:00', NULL, NULL, FALSE),
                                                                                                                      (8, '빈즈 정기 회의록', '회의록', '빈즈의 정기 회의 내용을 기록한 문서입니다.', 8, '2024-03-05 17:30:00', 39103, '2024-03-10 10:30:00', TRUE),
                                                                                                                      (9, '축구 경기 일정', '명단', '축구의 경기 일정입니다.', 9, '2024-03-10 11:00:00', 39101, '2024-03-15 14:00:00', TRUE),
                                                                                                                      (10, '농구 팀 구성 명단', '명단', '농구의 팀 구성 명단입니다.', 10, '2024-03-15 10:45:00', NULL, NULL, FALSE);
