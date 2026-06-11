package project

import db.Entity
import db.Id
import invoices.InvoiceId
import klite.Decimal
import klite.Decimal.Companion.ZERO
import users.User
import java.time.LocalDate

data class TimeEntry(
  val projectId: Id<Project>,
  val userId: Id<User> = Id(),
  val invoiceId: InvoiceId? = null,
  val date: LocalDate = LocalDate.now(),
  val hours: Decimal,
  val hourlyRate: Decimal = ZERO,
  val storyId: Long? = null,
  val description: String? = null,
  val activity: String? = null,
  override val id: Id<TimeEntry> = Id()
): Entity<TimeEntry>

data class TimeEntryView(
  val entry: TimeEntry,
  val customerName: String,
  val projectName: String,
  val userName: String
)
