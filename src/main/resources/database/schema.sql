
create table if not exists rights
(
  id serial not null
    constraint rights_pkey
      primary key,
  description varchar(255),
  name varchar(200) not null
    constraint uk_rights_name
      unique
);

create table if not exists roles
(
  id serial not null
    constraint roles_pkey
      primary key,
  name varchar(200) not null
    constraint uk_roles_name
      unique
);

create table if not exists roles_rights
(
  role_id integer not null
    constraint fk_role_id
      references roles,
  rights_id integer not null
    constraint fk_rights_id
      references rights,
  constraint roles_rights_pkey
    primary key (role_id, rights_id)
);


create table if not exists users
(
  id serial not null
    constraint users_pkey
      primary key,
  account_non_expired boolean not null,
  account_non_locked boolean not null,
  credentials_non_expired boolean not null,
  enabled boolean not null,
  first_name citext,
  last_badge_number_created integer not null,
  last_name citext,
  password varchar(255) not null,
  username varchar(200) not null
    constraint uk_username
      unique,
  role_id integer
    constraint fk_role_id
      references roles
);



create table if not exists blacklist
(
  id serial not null
    constraint blacklist_pkey
      primary key,
  first_name citext not null,
  last_name citext not null
);

create index if not exists blacklist_firstname_index
  on blacklist (first_name);

create index if not exists blacklist_lastname_index
  on blacklist (last_name);


create table if not exists tillsessions
(
  id serial not null
    constraint tillsessions_pkey
      primary key,
  end_time timestamp,
  open boolean not null,
  start_time timestamp,
  user_id integer not null
    constraint fkruie73rneumyyd1bgo6qw8vjt
      references users
);

create index if not exists tillsessions_open_index
  on tillsessions (open);



create table if not exists badges
(
  id serial not null
    constraint badges_pkey
      primary key,
  badge_type integer not null,
  badge_type_background_color varchar(255),
  badge_type_text varchar(255),
  name varchar(255) not null
    constraint uk_cuebofvgkgi4g9fxde2kmpr1h
      unique,
  required_right varchar(255),
  visible boolean not null,
  warning_message varchar(255)
);

create table if not exists ageranges
(
  id serial not null
    constraint ageranges_pkey
      primary key,
  cost numeric(19,2)
    constraint ageranges_cost_check
      check (cost >= (0)::numeric),
  max_age integer not null
    constraint ageranges_max_age_check
      check ((max_age >= 0) AND (max_age <= 255)),
  min_age integer not null
    constraint ageranges_min_age_check
      check ((min_age >= 0) AND (min_age <= 255)),
  name varchar(255) not null,
  stripe_color varchar(255),
  stripe_text varchar(255),
  badge_id integer
    constraint fk_ageranges_badge_id
      references badges
      on delete cascade
);




create table attendeehistory
(
  id serial not null
    constraint attendeehistory_pkey
      primary key,
  message citext,
  timestamp timestamp,
  user_id integer
    constraint fk_attendeehistory_userid_users
      references users,
  attendee_id integer
    constraint fk_attendeehisotry_attendeeid_attendees
      references attendees
);

alter table attendeehistory owner to kumoreg;

