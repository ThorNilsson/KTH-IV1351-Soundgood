DROP TABLE IF EXISTS instrument CASCADE;
DROP TABLE IF EXISTS person CASCADE;
DROP TABLE IF EXISTS phone CASCADE;
DROP TABLE IF EXISTS price CASCADE;
DROP TABLE IF EXISTS rental_instrument CASCADE;
DROP TABLE IF EXISTS student CASCADE;
DROP TABLE IF EXISTS adress CASCADE;
DROP TABLE IF EXISTS email CASCADE;
DROP TABLE IF EXISTS instructor CASCADE;
DROP TABLE IF EXISTS instructor_instrument CASCADE;
DROP TABLE IF EXISTS lesson CASCADE;
DROP TABLE IF EXISTS parent CASCADE;
DROP TABLE IF EXISTS rental CASCADE;
DROP TABLE IF EXISTS work_time CASCADE;
DROP TABLE IF EXISTS booking CASCADE;
DROP TABLE IF EXISTS ensamble_lesson CASCADE;
DROP TABLE IF EXISTS group_lesson CASCADE;
DROP TABLE IF EXISTS individual_lesson CASCADE;
DROP TABLE IF EXISTS job CASCADE;


CREATE TABLE instrument (
 id SERIAL PRIMARY KEY,
 name VARCHAR(500) NOT NULL,
 cathegory VARCHAR(500) NOT NULL,
 image VARCHAR(500),
 description VARCHAR(2000)
);

--ALTER TABLE instrument ADD CONSTRAINT PK_instrument PRIMARY KEY (id);

CREATE TABLE person (
 id SERIAL PRIMARY KEY,
 person_number VARCHAR(40) NOT NULL UNIQUE,
 user_role VARCHAR(40) NOT NULL,
 first_name VARCHAR(200),
 last_name VARCHAR(200)
);

--ALTER TABLE person ADD CONSTRAINT PK_person PRIMARY KEY (id);

CREATE TABLE phone (
 person_id INT NOT NULL,
 phone_number VARCHAR(50) NOT NULL
);

ALTER TABLE phone ADD CONSTRAINT PK_phone PRIMARY KEY (person_id,phone_number);


CREATE TABLE price (
 skill_level VARCHAR(20) NOT NULL,
 lesson_type VARCHAR(20) NOT NULL,
 price DOUBLE PRECISION,
 salary DOUBLE PRECISION,
 discount DOUBLE PRECISION
);

ALTER TABLE price ADD CONSTRAINT PK_price PRIMARY KEY (skill_level,lesson_type);


CREATE TABLE rental_instrument (
 id SERIAL PRIMARY KEY,
 model VARCHAR(100),
 notes VARCHAR(2000),
 monthly_price DOUBLE PRECISION,
 instrument_id INT NOT NULL
);

--ALTER TABLE rental_instrument ADD CONSTRAINT PK_rental_instrument PRIMARY KEY (id);


CREATE TABLE student (
 student_id INT NOT NULL,
 skill_level VARCHAR(20) NOT NULL,
 approved bool,
 instrument_id INT NOT NULL
);

ALTER TABLE student ADD CONSTRAINT PK_student PRIMARY KEY (student_id);


CREATE TABLE adress (
 person_id INT NOT NULL,
 street VARCHAR(100),
 zip VARCHAR(6),
 city VARCHAR(100)
);

ALTER TABLE adress ADD CONSTRAINT PK_adress PRIMARY KEY (person_id);


CREATE TABLE email (
 person_id INT NOT NULL,
 email VARCHAR(100) NOT NULL
);

ALTER TABLE email ADD CONSTRAINT PK_email PRIMARY KEY (person_id);


CREATE TABLE instructor (
 instructor_id INT NOT NULL,
 image VARCHAR(200),
 description VARCHAR(2000),
 ensemble_instructor bool
);

ALTER TABLE instructor ADD CONSTRAINT PK_instructor PRIMARY KEY (instructor_id);


CREATE TABLE instructor_instrument (
 instrument_id INT NOT NULL,
 instructor_id INT NOT NULL
);

ALTER TABLE instructor_instrument ADD CONSTRAINT PK_instructor_instrument PRIMARY KEY (instrument_id,instructor_id);


CREATE TABLE lesson (
 id SERIAL PRIMARY KEY,
 start_timestamp TIMESTAMP(13) NOT NULL,
 end_timestamp TIMESTAMP(13) NOT NULL,
 room VARCHAR(50),
 skill_level VARCHAR(20) NOT NULL,
 lesson_type VARCHAR(20) NOT NULL
);

--ALTER TABLE lesson ADD CONSTRAINT PK_lesson PRIMARY KEY (id);


CREATE TABLE parent (
 person_id INT NOT NULL,
 student_id INT NOT NULL
);

ALTER TABLE parent ADD CONSTRAINT PK_parent PRIMARY KEY (person_id,student_id);


CREATE TABLE rental (
 rental_instrument_id INT NOT NULL,
 student_id INT NOT NULL,
 start_date DATE NOT NULL,
 end_date DATE NOT NULL,
 delivery TIMESTAMP(13),
 price DOUBLE PRECISION,
 notes VARCHAR(2000),
 terminated bool,
 termination_date DATE
);

ALTER TABLE rental ADD CONSTRAINT PK_rental PRIMARY KEY (rental_instrument_id,student_id,start_date);


CREATE TABLE work_time (
 instructor_id INT NOT NULL,
 title VARCHAR(100),
 start_timestamp TIMESTAMP(13) NOT NULL,
 end_timestamp TIMESTAMP(13) NOT NULL
);

ALTER TABLE work_time ADD CONSTRAINT PK_work_time PRIMARY KEY (instructor_id,start_timestamp);


CREATE TABLE booking (
 lesson_id INT NOT NULL,
 student_id INT NOT NULL,
 price DOUBLE PRECISION,
 discount DOUBLE PRECISION,
 canceled bool
);

ALTER TABLE booking ADD CONSTRAINT PK_booking PRIMARY KEY (lesson_id,student_id);


CREATE TABLE ensamble_lesson (
 lesson_id INT NOT NULL,
 max_cap INT NOT NULL,
 min_cap INT NOT NULL,
 genre VARCHAR(100) NOT NULL
);

ALTER TABLE ensamble_lesson ADD CONSTRAINT PK_ensamble_lesson PRIMARY KEY (lesson_id);


CREATE TABLE group_lesson (
 lesson_id INT NOT NULL,
 max_cap INT NOT NULL,
 min_cap INT NOT NULL,
 instrument_id INT NOT NULL
);

ALTER TABLE group_lesson ADD CONSTRAINT PK_group_lesson PRIMARY KEY (lesson_id);


CREATE TABLE individual_lesson (
 lesson_id INT NOT NULL,
 instrument_id INT NOT NULL
);

ALTER TABLE individual_lesson ADD CONSTRAINT PK_individual_lesson PRIMARY KEY (lesson_id);


CREATE TABLE job (
 lesson_id INT NOT NULL,
 instructor_id INT NOT NULL,
 salary DOUBLE PRECISION NOT NULL
);

ALTER TABLE job ADD CONSTRAINT PK_job PRIMARY KEY (lesson_id,instructor_id);


ALTER TABLE phone ADD CONSTRAINT FK_phone_0 FOREIGN KEY (person_id) REFERENCES person (id);


ALTER TABLE rental_instrument ADD CONSTRAINT FK_rental_instrument_0 FOREIGN KEY (instrument_id) REFERENCES instrument (id);


ALTER TABLE student ADD CONSTRAINT FK_student_0 FOREIGN KEY (student_id) REFERENCES person (id);
ALTER TABLE student ADD CONSTRAINT FK_student_1 FOREIGN KEY (instrument_id) REFERENCES instrument (id);


ALTER TABLE adress ADD CONSTRAINT FK_adress_0 FOREIGN KEY (person_id) REFERENCES person (id);


ALTER TABLE email ADD CONSTRAINT FK_email_0 FOREIGN KEY (person_id) REFERENCES person (id);


ALTER TABLE instructor ADD CONSTRAINT FK_instructor_0 FOREIGN KEY (instructor_id) REFERENCES person (id);


ALTER TABLE instructor_instrument ADD CONSTRAINT FK_instructor_instrument_0 FOREIGN KEY (instrument_id) REFERENCES instrument (id);
ALTER TABLE instructor_instrument ADD CONSTRAINT FK_instructor_instrument_1 FOREIGN KEY (instructor_id) REFERENCES instructor (instructor_id);


ALTER TABLE lesson ADD CONSTRAINT FK_lesson_0 FOREIGN KEY (skill_level,lesson_type) REFERENCES price (skill_level,lesson_type);


ALTER TABLE parent ADD CONSTRAINT FK_parent_0 FOREIGN KEY (student_id) REFERENCES person (id);
ALTER TABLE parent ADD CONSTRAINT FK_parent_1 FOREIGN KEY (student_id) REFERENCES student (student_id);


ALTER TABLE rental ADD CONSTRAINT FK_rental_0 FOREIGN KEY (rental_instrument_id) REFERENCES rental_instrument (id);
ALTER TABLE rental ADD CONSTRAINT FK_rental_1 FOREIGN KEY (student_id) REFERENCES student (student_id);


ALTER TABLE work_time ADD CONSTRAINT FK_work_time_0 FOREIGN KEY (instructor_id) REFERENCES instructor (instructor_id);


ALTER TABLE booking ADD CONSTRAINT FK_booking_0 FOREIGN KEY (lesson_id) REFERENCES lesson (id);
ALTER TABLE booking ADD CONSTRAINT FK_booking_1 FOREIGN KEY (student_id) REFERENCES student (student_id);


ALTER TABLE ensamble_lesson ADD CONSTRAINT FK_ensamble_lesson_0 FOREIGN KEY (lesson_id) REFERENCES lesson (id);


ALTER TABLE group_lesson ADD CONSTRAINT FK_group_lesson_0 FOREIGN KEY (lesson_id) REFERENCES lesson (id);
ALTER TABLE group_lesson ADD CONSTRAINT FK_group_lesson_1 FOREIGN KEY (instrument_id) REFERENCES instrument (id);


ALTER TABLE individual_lesson ADD CONSTRAINT FK_individual_lesson_0 FOREIGN KEY (lesson_id) REFERENCES lesson (id);
ALTER TABLE individual_lesson ADD CONSTRAINT FK_individual_lesson_1 FOREIGN KEY (instrument_id) REFERENCES instrument (id);


ALTER TABLE job ADD CONSTRAINT FK_job_0 FOREIGN KEY (lesson_id) REFERENCES lesson (id);
ALTER TABLE job ADD CONSTRAINT FK_job_1 FOREIGN KEY (instructor_id) REFERENCES instructor (instructor_id);

