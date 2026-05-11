--changeset time_entry
create table time_entry(
  ${id},
  projectId int not null references projects(id) on delete cascade,
  projectMemberId int not null references project_members(id),
  date date not null default current_date,
  hours float not null,
  storyId int,
  description text,
  hourlyRate decimal not null
  );
