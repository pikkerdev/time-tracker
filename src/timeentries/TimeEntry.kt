package timeentries

import db.Entity
import db.Id
import invoices.InvoiceId
import klite.Decimal
import klite.Decimal.Companion.ZERO
import projects.Project
import projects.ProjectMember.Role
import projects.ProjectMember.Role.DEVELOPER
import users.User
import java.time.LocalDate

data class TimeEntry(
  val projectId: Id<Project>,
  val userId: Id<User> = Id(),
  val invoiceId: InvoiceId? = null,
  val date: LocalDate = LocalDate.now(),
  val hours: Decimal,
  val hourlyRate: Decimal = ZERO,
  val role: Role = DEVELOPER,
  val storyIds: Set<String> = emptySet(),
  val description: String? = null,
  val activity: String = "Development",
  override val id: Id<TimeEntry> = Id()
): Entity<TimeEntry>

data class TimeEntryView(
  val entry: TimeEntry,
  val customerName: String,
  val projectName: String,
  val userName: String
)
