CREATE TABLE users (
  id VARCHAR(100) CONSTRAINT pk_user_id PRIMARY KEY,
  email VARCHAR(100) NOT NULL,
  password VARCHAR(1000),
  enabled BOOLEAN NOT NULL DEFAULT true,
  last_login TIMESTAMP,
  non_locked BOOLEAN
);

CREATE TABLE authorities (
  user_id VARCHAR(100) CONSTRAINT fk_user_role_user_id REFERENCES users(id),
  role_id VARCHAR(100) NOT NULL,
  CONSTRAINT pk_user_id_role_id PRIMARY KEY (user_id, role_id)
);

create table password_reset_token (
  token varchar(100) constraint pk_password_reset_token primary key,
  user_id varchar(100) references users(id),
  expiry_date timestamp not null
);