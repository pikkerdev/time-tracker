--changeset users
create table users(
  ${id},
  firstName text not null,
  lastName text not null,
  role text not null,
  email text unique not null
);

--changeset users:createdAt
alter table users add column createdAt timestamptz not null default now();

--changeset users:updatedAt
alter table users add column updatedAt timestamptz not null default now();

--changeset users:avatarUrl
alter table users add column avatarUrl text default null;

--changeset users:rename-role-to-authRole
alter table users rename column role to authRole;

--changeset users:rename-authrole-user-to-internal
update users set authrole = 'INTERNAL', updatedat = NOW() WHERE authrole = 'USER';

--changeset users.phone
alter table users add column phone text;
