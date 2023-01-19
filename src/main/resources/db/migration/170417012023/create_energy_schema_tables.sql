CREATE TABLE energy.profile (
    id bigserial PRIMARY KEY,
    meter_id varchar(255) NULL,
    "name" varchar(255) NULL UNIQUE
);


CREATE TABLE energy.fraction (
    id bigserial PRIMARY KEY,
    "month" int4 NULL,
    value NUMERIC(2, 2) NULL,
    "year" int4 NULL,
    profile_id int8 NOT NULL,
    UNIQUE(profile_id, "month", "year")
);


ALTER TABLE energy.fraction ADD CONSTRAINT fkm29yq50qg7b3fm7w20bp7ihfx FOREIGN KEY (profile_id) REFERENCES energy.profile(id);


CREATE TABLE energy.meter_measurement (
    id bigserial PRIMARY KEY,
    "month" int4 NULL,
    value int4 NULL,
    "year" int4 NULL,
    profile_id int8 NOT NULL,
    UNIQUE(profile_id, "month", "year")
);


ALTER TABLE energy.meter_measurement ADD CONSTRAINT fkm50hp89u6mm9147te7ecbtr8v FOREIGN KEY (profile_id) REFERENCES energy.profile(id);