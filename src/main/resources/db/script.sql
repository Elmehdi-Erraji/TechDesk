-- Create APP_USERS table
CREATE TABLE APP_USERS (
    ID       RAW(16)            NOT NULL PRIMARY KEY,
    PASSWORD VARCHAR2(255 CHAR)   NOT NULL,
    ROLE     VARCHAR2(255 CHAR)   NOT NULL
        CHECK (ROLE IN ('ADMIN', 'EMPLOYEE', 'IT_SUPPORT')),
    USERNAME VARCHAR2(50 CHAR)    NOT NULL
        CONSTRAINT UKSPSNWR241E9K9C8P5XL4K45IH UNIQUE
);
/

-- Create TICKETS table
CREATE TABLE TICKETS (
    ID          RAW(16)             NOT NULL PRIMARY KEY,
    CATEGORY    VARCHAR2(255 CHAR)  NOT NULL CHECK (CATEGORY IN ('NETWORK', 'HARDWARE', 'SOFTWARE', 'OTHER')),
    CREATED_AT  TIMESTAMP(6)        NOT NULL,
    DESCRIPTION VARCHAR2(2000 CHAR) NOT NULL,
    PRIORITY    VARCHAR2(255 CHAR)  NOT NULL CHECK (PRIORITY IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    STATUS      VARCHAR2(255 CHAR)  NOT NULL CHECK (STATUS IN ('NEW', 'IN_PROGRESS', 'RESOLVED', 'CLOSED')),
    TITLE       VARCHAR2(100 CHAR)  NOT NULL,
    UPDATED_AT  TIMESTAMP(6),
    ASSIGNED_TO RAW(16) CONSTRAINT FK8QDL5563I4OEDORMQA9IBOQXM REFERENCES APP_USERS,
    CREATED_BY  RAW(16) NOT NULL CONSTRAINT FKJM2E5HMCL3L28M1K9BVQR529J REFERENCES APP_USERS
);
/

-- Create COMMENTS table
CREATE TABLE COMMENTS (
    ID         RAW(16)             NOT NULL PRIMARY KEY,
    CREATED_AT TIMESTAMP(6)        NOT NULL,
    TEXT       VARCHAR2(1000 CHAR) NOT NULL,
    TICKET_ID  RAW(16)             NOT NULL
        CONSTRAINT FKJ7VC0AIGR2M5MEW52V7DDT4FO REFERENCES TICKETS,
    USER_ID    RAW(16)             NOT NULL
        CONSTRAINT FK8WYOM37KC61AD0JBHA24MIOO8 REFERENCES APP_USERS
);
/

-- Create TICKET_AUDIT_LOGS table
CREATE TABLE TICKET_AUDIT_LOGS (
    ID          RAW(16)             NOT NULL PRIMARY KEY,
    DESCRIPTION VARCHAR2(2000 CHAR) NOT NULL,
    LOG_TYPE    VARCHAR2(255 CHAR)  NOT NULL CHECK (LOG_TYPE IN ('STATUS_CHANGE', 'COMMENT_ADDED')),
    TIMESTAMP   TIMESTAMP(6)        NOT NULL,
    CHANGED_BY  RAW(16)             NOT NULL
        CONSTRAINT FKS7AY8X6CD1WOQE6IY8PTKW255 REFERENCES APP_USERS,
    TICKET_ID   RAW(16)             NOT NULL
        CONSTRAINT FKBMO6OBTLQCHTCFB31157KJDE REFERENCES TICKETS
);
/



-- Migrations
INSERT INTO APP_USERS (ID, PASSWORD, ROLE, USERNAME)
VALUES (
           HEXTORAW('F9A3E9C8E19A461EA5471350E44F6DD9'),
           '$2a$10$HOAj6pqCL/1ZpXdRR2bwgOd.p3jfAvnsCz6D0nqbaiC92LMYYd0Gy',
           'ADMIN',
           'admin'
       );

INSERT INTO APP_USERS (ID, PASSWORD, ROLE, USERNAME)
VALUES (
           HEXTORAW('F9A3E9C8E19A461EA5471350E44F6DDA'),
           '$2a$10$HOAj6pqCL/1ZpXdRR2bwgOd.p3jfAvnsCz6D0nqbaiC92LMYYd0Gy',
           'IT_SUPPORT',
           'support'
       );

INSERT INTO APP_USERS (ID, PASSWORD, ROLE, USERNAME)
VALUES (
           HEXTORAW('F9A3E9C8E19A461EA5471350E44F6DDB'),
           '$2a$10$HOAj6pqCL/1ZpXdRR2bwgOd.p3jfAvnsCz6D0nqbaiC92LMYYd0Gy',
           'EMPLOYEE',
           'employee'
       );


-- Tickets migration

-- Migration: Insert 10 tickets into the TICKETS table

-- Ticket 1
INSERT INTO TICKETS (
    ID, CATEGORY, CREATED_AT, DESCRIPTION, PRIORITY, STATUS, TITLE, UPDATED_AT, ASSIGNED_TO, CREATED_BY
) VALUES (
             HEXTORAW('F9A3E9C8E19A461EA5471350E44F6DDE'),
             'NETWORK',
             TIMESTAMP '2025-02-25 08:15:00',
             'Network outage reported in the main office.',
             'HIGH',
             'NEW',
             'Network Outage',
             NULL,
             NULL,
             HEXTORAW('F9A3E9C8E19A461EA5471350E44F6DD9')
         );

-- Ticket 2
INSERT INTO TICKETS (
    ID, CATEGORY, CREATED_AT, DESCRIPTION, PRIORITY, STATUS, TITLE, UPDATED_AT, ASSIGNED_TO, CREATED_BY
) VALUES (
             HEXTORAW('F9A3E9C8E19A461EA5471350E44F6DDF'),
             'HARDWARE',
             TIMESTAMP '2025-02-25 09:00:00',
             'Printer malfunction in the reception area.',
             'MEDIUM',
             'IN_PROGRESS',
             'Printer Issue',
             NULL,
             NULL,
             HEXTORAW('F9A3E9C8E19A461EA5471350E44F6DD9')
         );

-- Ticket 3
INSERT INTO TICKETS (
    ID, CATEGORY, CREATED_AT, DESCRIPTION, PRIORITY, STATUS, TITLE, UPDATED_AT, ASSIGNED_TO, CREATED_BY
) VALUES (
             HEXTORAW('F9A3E9C8E19A461EA5471350E44F6DE0'),
             'SOFTWARE',
             TIMESTAMP '2025-02-25 09:30:00',
             'Software update required for the accounting system.',
             'CRITICAL',
             'NEW',
             'Software Update',
             NULL,
             NULL,
             HEXTORAW('F9A3E9C8E19A461EA5471350E44F6DD9')
         );

-- Ticket 4
INSERT INTO TICKETS (
    ID, CATEGORY, CREATED_AT, DESCRIPTION, PRIORITY, STATUS, TITLE, UPDATED_AT, ASSIGNED_TO, CREATED_BY
) VALUES (
             HEXTORAW('F9A3E9C8E19A461EA5471350E44F6DE1'),
             'OTHER',
             TIMESTAMP '2025-02-25 10:00:00',
             'General inquiry about IT policies.',
             'LOW',
             'RESOLVED',
             'IT Policy Inquiry',
             NULL,
             NULL,
             HEXTORAW('F9A3E9C8E19A461EA5471350E44F6DD9')
         );

-- Ticket 5
INSERT INTO TICKETS (
    ID, CATEGORY, CREATED_AT, DESCRIPTION, PRIORITY, STATUS, TITLE, UPDATED_AT, ASSIGNED_TO, CREATED_BY
) VALUES (
             HEXTORAW('F9A3E9C8E19A461EA5471350E44F6DE2'),
             'NETWORK',
             TIMESTAMP '2025-02-25 10:15:00',
             'Slow internet speed in the west wing.',
             'HIGH',
             'NEW',
             'Internet Speed Issue',
             NULL,
             NULL,
             HEXTORAW('F9A3E9C8E19A461EA5471350E44F6DD9')
         );

-- Ticket 6
INSERT INTO TICKETS (
    ID, CATEGORY, CREATED_AT, DESCRIPTION, PRIORITY, STATUS, TITLE, UPDATED_AT, ASSIGNED_TO, CREATED_BY
) VALUES (
             HEXTORAW('F9A3E9C8E19A461EA5471350E44F6DE3'),
             'HARDWARE',
             TIMESTAMP '2025-02-25 10:30:00',
             'Laptop battery issue reported by an employee.',
             'MEDIUM',
             'IN_PROGRESS',
             'Laptop Battery Issue',
             NULL,
             NULL,
             HEXTORAW('F9A3E9C8E19A461EA5471350E44F6DD9')
         );

-- Ticket 7
INSERT INTO TICKETS (
    ID, CATEGORY, CREATED_AT, DESCRIPTION, PRIORITY, STATUS, TITLE, UPDATED_AT, ASSIGNED_TO, CREATED_BY
) VALUES (
             HEXTORAW('F9A3E9C8E19A461EA5471350E44F6DE4'),
             'SOFTWARE',
             TIMESTAMP '2025-02-25 11:00:00',
             'Email client crashes intermittently.',
             'CRITICAL',
             'NEW',
             'Email Client Crash',
             NULL,
             NULL,
             HEXTORAW('F9A3E9C8E19A461EA5471350E44F6DD9')
         );

-- Ticket 8
INSERT INTO TICKETS (
    ID, CATEGORY, CREATED_AT, DESCRIPTION, PRIORITY, STATUS, TITLE, UPDATED_AT, ASSIGNED_TO, CREATED_BY
) VALUES (
             HEXTORAW('F9A3E9C8E19A461EA5471350E44F6DE5'),
             'OTHER',
             TIMESTAMP '2025-02-25 11:15:00',
             'Request for new workstation setup.',
             'LOW',
             'IN_PROGRESS',
             'Workstation Setup Request',
             NULL,
             NULL,
             HEXTORAW('F9A3E9C8E19A461EA5471350E44F6DD9')
         );

-- Ticket 9
INSERT INTO TICKETS (
    ID, CATEGORY, CREATED_AT, DESCRIPTION, PRIORITY, STATUS, TITLE, UPDATED_AT, ASSIGNED_TO, CREATED_BY
) VALUES (
             HEXTORAW('F9A3E9C8E19A461EA5471350E44F6DE6'),
             'NETWORK',
             TIMESTAMP '2025-02-25 11:30:00',
             'VPN connectivity issues reported by a remote employee.',
             'HIGH',
             'NEW',
             'VPN Connectivity Issue',
             NULL,
             NULL,
             HEXTORAW('F9A3E9C8E19A461EA5471350E44F6DD9')
         );

-- Ticket 10
INSERT INTO TICKETS (
    ID, CATEGORY, CREATED_AT, DESCRIPTION, PRIORITY, STATUS, TITLE, UPDATED_AT, ASSIGNED_TO, CREATED_BY
) VALUES (
             HEXTORAW('F9A3E9C8E19A461EA5471350E44F6DE7'),
             'HARDWARE',
             TIMESTAMP '2025-02-25 12:00:00',
             'Desktop computer overheating problem in the finance department.',
             'MEDIUM',
             'NEW',
             'Overheating Desktop',
             NULL,
             NULL,
             HEXTORAW('F9A3E9C8E19A461EA5471350E44F6DD9')
         );
