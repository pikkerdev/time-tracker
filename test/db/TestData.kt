package db

import customers.Customer
import klite.Email
import klite.d
import project.*
import project.ProjectMember.Role.DEVELOPER
import users.AuthRole.ADMIN
import users.AuthRole.EXTERNAL
import users.User
import java.time.LocalDate
import java.time.ZoneOffset.UTC

/** Immutable domain object samples for unit tests */
object TestData {
  val date = LocalDate.of(2025, 3, 3)
  val now = date.atStartOfDay().toInstant(UTC)

  val admin = User("Admin", "Admin", Email("admin@test.ee"), authRole = ADMIN , createdAt = now)
  val user = User("User", "User", Email("user@test.ee"), authRole = EXTERNAL, createdAt = now)

  val customer = Customer(Id(), "Customer1")
  val project = Project(Id(1), customer.id, "Project1", currency = "EUR", hourlyRates = mapOf(DEVELOPER to 88.d), customerName = customer.name)
  val projectMember = ProjectMember(project.id, user.id, DEVELOPER, createdAt = now)
  val timeEntry = TimeEntry(project.id, user.id, date, 8f, hourlyRate = 88.d, id = Id(),)
  val projectMemberUser = ProjectMemberUser(projectMember, user)
}
