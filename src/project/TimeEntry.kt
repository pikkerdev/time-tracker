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
  val hours: Float,
  val storyId: BigInteger? = null,
  val description: String? = null,
  val hourlyRate: Decimal = Decimal.ZERO,
  override val id: Id<TimeEntry> = Id()
): Entity<TimeEntry>
