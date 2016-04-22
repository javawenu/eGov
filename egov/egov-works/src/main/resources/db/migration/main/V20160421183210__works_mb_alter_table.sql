------------------START------------------
ALTER TABLE EGW_MB_HEADER DROP COLUMN IF EXISTS IS_LEGACY_MB;
ALTER TABLE EGW_MB_HEADER ADD COLUMN IS_LEGACY_MB boolean DEFAULT 'false';
ALTER TABLE EGW_MB_HEADER ALTER COLUMN MB_AMOUNT TYPE double precision;

-------------------END-------------------

--rollback ALTER TABLE EGW_MB_HEADER ALTER COLUMN MB_AMOUNT TYPE bigint;
--rollback ALTER TABLE EGW_MB_HEADER DROP COLUMN IF EXISTS IS_LEGACY_MB;
--rollback ALTER TABLE EGW_MB_HEADER ADD COLUMN IS_LEGACY_MB smallint;