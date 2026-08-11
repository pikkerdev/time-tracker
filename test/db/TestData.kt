package db

import customers.Customer
import db.Status.DELETED
import invoices.*
import klite.Email
import klite.d
import projects.*
import projects.ProjectMember.Role.DEVELOPER
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
  val customer2 = Customer("Customer2", status = DELETED)
  val project = Project(customer.id, "Project1", hourlyRates = mapOf(DEVELOPER to 88.d), customerName = customer.name)
  val project2 = Project(customer.id, "Project2", hourlyRates = mapOf(DEVELOPER to 44.d), customerName = customer.name)
  val project3 = Project(customer.id, "Project3", hourlyRates = mapOf(DEVELOPER to 44.d), customerName = customer.name, status = DELETED)
  val projectStats = ProjectStats(10.d, 20.d, 30.d, 40.d)
  val projectView = ProjectView(project, projectStats)
  val projectMember = ProjectMember(project.id, user.id, DEVELOPER, createdAt = now)
  val projectMember2 = ProjectMember(project2.id, user.id, DEVELOPER, createdAt = now)
  val projectMemberUser = ProjectMemberUser(projectMember, user)
  val invoice = Invoice(InvoiceId(2026060101), project.id, LocalDate.of(2026, 6, 1),"Invoice 1", description = "development", dueDate = LocalDate.of(2026, 6, 14), revenueMonth = LocalDate.of(2026, 5, 1), rows = listOf(
    InvoiceRow(description = "Invoice row", hours = 3.d, rate = 5.d, amount = 15.d )))
  val timeEntry = TimeEntry(project.id, user.id, invoiceId = null, date, 7.5.d, hourlyRate = 88.d, DEVELOPER)
  val timeEntry2 = TimeEntry(project.id, user.id, hours = 4.d, hourlyRate = 60.d, role = DEVELOPER)
  val timeEntry3 = TimeEntry(project3.id, user.id, hours = 4.5.d, hourlyRate = 60.d, role = DEVELOPER)
  val timeEntryView = TimeEntryView(timeEntry, customer.name, project.name, user.name)
  val invoiceCreateRequest = InvoiceCreateRequest(LocalDate.of(2026, 6, 1), listOf(timeEntry.id, timeEntry2.id),"Invoice 1", description= "development", dueDate = LocalDate.of(2026, 6, 14), revenueMonth = LocalDate.of(2026, 5, 1), rows = listOf(
    InvoiceRow(description = "Invoice row", hours = 3.d, rate = 5.d, amount = 15.d )))
  val invoiceView = InvoiceView(invoice, user.name, customer.name, project.name)
  val invoiceWithIds = InvoiceWithIds(invoice, customer.id, user.id)
  val invoiceDetails = InvoiceDetails(invoice, customer, user, 0.24.d)
}
