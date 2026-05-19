package project

import db.Entity
import db.Id
import klite.Decimal
import users.User
import java.math.BigInteger
import java.time.LocalDate

data class TimeEntry(
  val projectId: Id<Project>,
  val userId: Id<User> = Id(),
  val date: LocalDate = LocalDate.now(),
  val hours: Decimal,
  val hourlyRate: Decimal,
  val storyId: Long? = null,
  val description: String? = null,
  override val id: Id<TimeEntry> = Id()
): Entity<TimeEntry>

data class TimeEntryView(
  val entry: TimeEntry,
  val customerName: String,
  val projectName: String,
  val userName: String
)
