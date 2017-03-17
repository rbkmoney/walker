create schema if not exists walk;

create table walk.claim (
  id BIGINT not null,
  eventId BIGINT not null,
  assigned CHARACTER VARYING NOT NULL,
  changes JSONB not null,
  constraint claim_id primary key (id)
);
