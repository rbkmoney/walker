ALTER TABLE WALK.ACTION ADD COLUMN event_created_at CHARACTER VARYING;

CREATE UNIQUE INDEX ACTION_idx on WALK.ACTION(claim_id, party_id, event_created_at, type);