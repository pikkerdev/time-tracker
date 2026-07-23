--changeset invoices
create table invoices(
  ${id},
  customerId bigint not null references customers(id) on delete cascade,
  projectId bigint not null references projects(id) on delete cascade,
  timeEntryIds bigint[] not null,
  number text not null unique,
  amount decimal not null,
  vatAmount decimal not null,
  totalAmount decimal not null,
  createdAt date not null default now()::date
);

--changeset invoices.audit
alter table invoices drop column timeEntryIds;
alter table invoices drop column totalAmount;

--changeset invoices.alter
alter table invoices drop column number,
  drop column createdAt,
  add column date date not null default current_date,
  add column createdAt timestamptz not null default now(),
  add column createdBy bigint default get_app_user();

--changeset invoices.customerId:drop
alter table invoices drop column customerId;

--changeset invoices_project_idx
create index on invoices(projectId);

--changeset invoices.alter-add-description-comment
alter table invoices add column description text not null default '',
   add column comment text not null default '';

--changeset invoices.alter-add-dueDate-drop-comment
alter table invoices add column dueDate date,
  drop column comment;

--changeset invoices.update-dueDate
update invoices set dueDate = date + interval '14 days' where dueDate is null;

--changeset invoices.set-dueDate-not-null
alter table invoices alter column dueDate set not null;

--changeset invoices:app-can-delete
grant delete on invoices to app;

--changeset invoices.updatedAt
alter table invoices add column updatedAt timestamptz not null default now();

--changeset invoices.status
alter table invoices add column status text not null default 'CREATED';

--changeset invoices.rows-jsonb
alter table invoices add column rows jsonb not null default '[]';

--changeset invoices:amount
alter table invoices drop column amount;

--changeset invoices:vatAmount
alter table invoices drop column vatAmount;


