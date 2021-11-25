drop table if exists license_agreement;

create table license_agreement (
    description text not null,
    expiry_date timestamptz not null
);
