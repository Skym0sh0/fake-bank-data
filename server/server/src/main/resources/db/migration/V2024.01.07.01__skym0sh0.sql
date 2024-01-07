CREATE TABLE USERS
(
    ID         UUID                     NOT NULL UNIQUE,

    USERNAME   TEXT                     NOT NULL UNIQUE,
    PASSWORD   TEXT                     NOT NULL,

    ROLES      TEXT                     NOT NULL,

    CREATED_AT TIMESTAMP WITH TIME ZONE NOT NULL,
    UPDATED_AT TIMESTAMP WITH TIME ZONE NOT NULL,

    PRIMARY KEY (ID)
);

INSERT INTO USERS
VALUES (uuid_generate_v4(), 'default_user', '{noop}password1234', 'ROLE_USER', now(), now());
