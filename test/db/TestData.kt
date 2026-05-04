package db

import customers.Customer
import klite.Email
import project.Project
import project.ProjectMember
import project.ProjectMemberUser
import project.Role
import users.AuthRole.EXTERNAL
import users.AuthRole.ADMIN
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
  val project = Project(Id(1), customer.id, "Project1", currency = "EUR", hourlyRate = 10.toBigDecimal())
  val projectMember = ProjectMember(project.id, user.id, Role.DEVELOPER, createdAt = now)
  val projectMemberUser = ProjectMemberUser(projectMember, user)
}
