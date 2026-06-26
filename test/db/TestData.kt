package db

import customers.Customer
import invoices.*
import klite.Email
import klite.d
import projects.Project
import projects.Project.Status.DELETED
import projects.ProjectMember
import projects.ProjectMember.Role.DEVELOPER
import projects.ProjectMemberUser
import timeentries.TimeEntry
import timeentries.TimeEntryView
import users.AuthRole.ADMIN
import users.AuthRole.EXTERNAL
import users.User
import java.time.LocalDate
import java.time.ZoneOffset.UTC

/** Immutable domain object samples for unit tests */
object TestData {
  val today = LocalDate.now()
  val yesterday = today.minusDays(1)
  val twoDaysAgo = today.minusDays(2)

  val date = LocalDate.of(2026, 3, 3)
  val now = date.atStartOfDay().toInstant(UTC)

  val admin = User("Admin", "Admin", Email("admin@test.ee"), authRole = ADMIN , createdAt = now)
  val user = User("User", "User", Email("user@test.ee"), authRole = EXTERNAL, createdAt = now)

  val customer = Customer("Customer1")
  val project = Project(customer.id, "Project1", hourlyRates = mapOf(DEVELOPER to 88.d), customerName = customer.name)
  val project2 = Project(customer.id, "Project2", hourlyRates = mapOf(DEVELOPER to 44.d), customerName = customer.name)
  val project3 = Project(customer.id, "Project3", hourlyRates = mapOf(DEVELOPER to 44.d), customerName = customer.name, status = DELETED)
  val projectMember = ProjectMember(project.id, user.id, DEVELOPER, createdAt = now)
  val projectMember2 = ProjectMember(project2.id, user.id, DEVELOPER, createdAt = now)
  val projectMemberUser = ProjectMemberUser(projectMember, user)
  val invoice = Invoice(InvoiceId(2026060101), project.id, LocalDate.of(2026, 6, 1), 760.d, 240.d, description = "development", dueDate = LocalDate.of(2026, 6, 14))
  val timeEntry = TimeEntry(project.id, user.id, invoiceId = null, date, 7.5.d, hourlyRate = 88.d, DEVELOPER)
  val timeEntry2 = TimeEntry(project.id, user.id, hours = 4.d, hourlyRate = 60.d, role = DEVELOPER)
  val timeEntry3 = TimeEntry(project3.id, user.id, hours = 4.5.d, hourlyRate = 60.d, role = DEVELOPER)
  val timeEntryView = TimeEntryView(timeEntry, customer.name, project.name, user.name)
  val invoiceCreateRequest = InvoiceCreateRequest(LocalDate.of(2026, 6, 1), listOf(timeEntry.id, timeEntry2.id), description= "development", dueDate = LocalDate.of(2026, 6, 14))
  val invoiceView = InvoiceView(invoice, user.name, customer.name, project.name)
  val invoiceWithCustomer = InvoiceWithCustomer(invoice, customer.id)
  val rolesHoursEntry = RoleHoursEntry(DEVELOPER, 10.d, 100.d)
  val invoiceDetails = InvoiceDetails(invoice, customer, listOf(rolesHoursEntry), 0.24.d)
}
