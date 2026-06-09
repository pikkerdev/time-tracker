--changeset projects
create table projects(
  ${id},
  customerId bigint not null references customers(id),
  name text not null,
  description text,
  currency text not null default 'EUR',
  hourlyRate decimal not null,
  storyTrackerId int
);

--changeset projects.audit
create trigger projects_history after update on projects for each row execute function add_change_history();

--changeset projects:createdAt
alter table projects add column createdAt timestamptz not null default now();

--changeset projects:unique_customer_project_name
alter table projects add constraint projects_unique_customer_name unique (customerId, name);

--changeset projects:hourlyRates
alter table projects drop column hourlyRate;
alter table projects add column hourlyRates jsonb not null default '{}'::jsonb;

--changeset projects_customers_idx
create index on projects(customerId);

--changeset projects:stortyTrackerId:bigint
alter table projects alter column storyTrackerId type bigint using storyTrackerId::bigint;

--changeset projects.tags
alter table projects add column tags text[] default '{}' not null;
