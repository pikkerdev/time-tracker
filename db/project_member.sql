--changeset project_members
create table project_members(
  ${id},
  projectId int not null references projects(id) on delete cascade,
  userId int not null references users(id) on delete cascade,
  role text not null default 'ADMIN',
  updatedAt timestamptz not null default now(),
  createdAt timestamptz not null default now(),
  unique (projectId, userId)
);

--changeset project_members.projectId:bigint
alter table project_members alter column projectId type bigint using projectId::bigint;

--changeset project_members.userId:bigint
alter table project_members alter column userId type bigint using userId::bigint;

--changeset project_members:default-role-to-developer
alter table project_members alter column role set default 'DEVELOPER';

--changeset project_member:status
alter table project_members add column status text not null default 'ACTIVE';

--changeset project_members_project_idx
create index on project_members(projectId);

--changeset project_members_user_idx
create index on project_members(userId);
