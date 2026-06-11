package db

import project.TimeEntry
import project.TimeEntryView
import customers.Customer
import invoices.Invoice
import invoices.InvoiceCreateRequest
import invoices.InvoiceId
import klite.Email
import klite.d
import project.*
import project.Project.Status.DELETED
import project.ProjectMember.Role.DEVELOPER
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
  val projectMemberUser = ProjectMemberUser(projectMember, user)
  val invoice = Invoice(InvoiceId(2026060101), project.id, LocalDate.of(2026, 6, 1), 760.d, 240.d, description = "development", dueDate = LocalDate.of(2026, 6, 14))
  val timeEntry = TimeEntry(project.id, user.id, invoiceId = null, date, 7.5.d, hourlyRate = 88.d)
  val timeEntry2 = TimeEntry(id = Id(), hours = 4.d, hourlyRate = 60.d, projectId = project.id)
  val timeEntryView = TimeEntryView(timeEntry, customer.name, project.name, user.name)
  val invoiceCreateRequest = InvoiceCreateRequest(LocalDate.of(2026, 6, 1), listOf(timeEntry.id, timeEntry2.id), description= "development", dueDate = LocalDate.of(2026, 6, 14))

}
