package projects

import customers.Customer
import db.Entity
import db.Id
import db.Status
import db.Status.ACTIVE
import klite.Decimal
import klite.jdbc.JsonColumn
import klite.jdbc.UpdatableEntity
import klite.json.JsonProperty
import java.time.Instant
import java.time.LocalDate
import java.util.*

val EUR: Currency = Currency.getInstance("EUR")

data class Project(
  val customerId: Id<Customer>,
  val name: String,
  val description: String? = null,
  @JsonColumn val hourlyRates: Map<ProjectMember.Role, Decimal>,
  val currency: Currency = EUR,
  val storyTrackerId: Long? = null,
  val activities: Set<String> = setOf("Development", "Meeting", "Consultancy", "Testing", "Research", "Support"),
  override var updatedAt: Instant? = null,
  val status: Status = ACTIVE,
  val color: String = "#D7A262",
  @JsonProperty(readOnly = true) val customerName: String? = null,
  override val id: Id<Project> = Id()
): Entity<Project>, UpdatableEntity

data class MonthlyStats(
  val billedHours: Decimal,
  val unbilledHours: Decimal,
  val billedRevenue: Decimal,
  val unbilledRevenue: Decimal,
)

data class ProjectView(
  val project: Project,
  @JsonColumn val stats: Map<LocalDate, MonthlyStats>
)
