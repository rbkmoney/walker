CREATE TABLE WALK.LAST_EVENT_ID (
  id BIGINT
);

INSERT INTO WALK.LAST_EVENT_ID(id) SELECT MAX(event_id) FROM WALK.CLAIM;