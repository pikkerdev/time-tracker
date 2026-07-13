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

--changeset time_entry.tags
alter table time_entry add column tags text[] default '{}' not null;

--changeset time_entry.invoiceId
alter table time_entry add column invoiceId bigint references invoices(id);

--changeset time_entry:tags
alter table time_entry drop column tags;

--changeset time_entry.tag
alter table time_entry add column tag text;

--changeset time_entry:rename-tag-to-activity
alter table time_entry rename column tag to activity;

--changeset time_entry.invoiceId-on-delete-null
alter table time_entry drop constraint time_entry_invoiceid_fkey;
alter table time_entry add constraint time_entry_invoiceid_fkey foreign key (invoiceId) references invoices(id) on delete set null;

--changeset time_entry.role
alter table time_entry add column role text;
update time_entry te set role = pm.role from project_members pm where te.projectId = pm.projectId and te.userId = pm.userId;

--changeset time_entry.modify-storyId-to-array
alter table time_entry alter column storyId type bigint[] using case when storyId is null then '{}'::bigint[] else ARRAY[storyId] end,
alter column storyId set default '{}', alter column storyId set not null;

--changeset time_entry:grant-delete
grant delete on time_entry to app;

--changeset time_entry.activity-default-development
alter table time_entry alter column activity type text using case when activity is null then 'Development' else activity end;
alter table time_entry alter column activity set default 'Development';
alter table time_entry alter column activity set not null;

--changeset time_entry:rename-storyId-to-storyIds
alter table time_entry rename column storyId to storyIds;

--changeset time_entry.modify-storyId-to-text-array
alter table time_entry alter column storyIds type text[] using storyIds::text[];
alter table time_entry alter column storyIds set default '{}';

