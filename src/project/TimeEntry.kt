package project

import db.Entity
import db.Id
import users.User
import java.math.BigDecimal
import java.time.LocalDate

data class TimeEntry(
  val projectId: Id<Project>,
  val userId: Id<User>,
  val date: LocalDate = LocalDate.now(),
  val hours: Float,
  val storyId: Int? = null,
  val description: String? = null,
  val hourlyRate: BigDecimal,
  override val id: Id<TimeEntry> = Id()
): Entity<TimeEntry>
