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
)
