CREATE SCHEMA IF NOT EXISTS WALK;

CREATE TABLE WALK.CLAIM (
  id               BIGINT            NOT NULL,
  event_id         BIGINT            NOT NULL,
  revision         BIGINT            NOT NULL,
  party_id         CHARACTER VARYING NOT NULL,
  assigned_user_id CHARACTER VARYING NOT NULL,
  status           CHARACTER VARYING NOT NULL,
  description      CHARACTER VARYING,
  reason           CHARACTER VARYING,
  damsel_version   CHARACTER VARYING NOT NULL,
  changes          JSONB
);

CREATE INDEX walk_claim_id_party
  ON WALK.CLAIM (party_id, id);

CREATE TABLE WALK.ACTION (
  id         BIGSERIAL         NOT NULL,
  claim_id   BIGINT            NOT NULL,
  party_id   CHARACTER VARYING NOT NULL,
  created_at TIMESTAMP         NOT NULL,
  user_id    CHARACTER VARYING NOT NULL,
  user_name  CHARACTER VARYING,
  user_email CHARACTER VARYING,
  type       CHARACTER VARYING NOT NULL,
  before     CHARACTER VARYING,
  after      CHARACTER VARYING NOT NULL
);


CREATE TABLE WALK.COMMENT (
  id         BIGSERIAL         NOT NULL,
  claim_id   BIGINT            NOT NULL,
  party_id   CHARACTER VARYING NOT NULL,
  text       CHARACTER VARYING NOT NULL,
  created_at TIMESTAMP         NOT NULL,
  user_id    CHARACTER VARYING NOT NULL,
  user_name  CHARACTER VARYING,
  email      CHARACTER VARYING
);

