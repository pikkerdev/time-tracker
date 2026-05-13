--changeset time_entry onChange:RUN
drop table if exists time_entry;
create table time_entry(
  ${id},
  projectId bigint not null references projects(id) on delete cascade,
  userId bigint not null references users(id) on delete cascade,
  date date not null default current_date,
  hours float not null,
  hourlyRate decimal not null,
  storyId bigint,
  description text
);

--changeset time_entry.audit
create trigger time_entry_history after update on time_entry for each row execute function add_change_history();

--changeset time_entry_project_idx
create index on time_entry(projectId);

--changeset time_entry_user_idx
create index on time_entry(userId);
