package project

import db.Entity
import db.Id
import java.math.BigDecimal
import java.time.LocalDate

data class TimeEntry(
  val projectId: Id<Project>,
  val projectMemberId: Id<ProjectMember>,
  val date: LocalDate = LocalDate.now(),
  val hours: Float,
  val storyId: Int? = null,
  val description: String? = null,
  val hourlyRate: BigDecimal,
  override val id: Id<TimeEntry> = Id()
): Entity<TimeEntry>
