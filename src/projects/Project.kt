package projects

import customers.Customer
import db.Entity
import db.Id
import klite.Decimal
import klite.jdbc.UpdatableEntity
import klite.json.JsonProperty
import projects.Project.Status.ACTIVE
import java.time.Instant
import java.util.*

val EUR: Currency = Currency.getInstance("EUR")

data class Project(
  val customerId: Id<Customer>,
  val name: String,
  val description: String? = null,
  val hourlyRates: Map<ProjectMember.Role, Decimal>,
  val currency: Currency = EUR,
  val storyTrackerId: Long? = null,
  val activities: Set<String> = setOf("Development", "Meeting", "Consultancy", "Testing", "Research", "Support"),
  override var updatedAt: Instant? = null,
  val status: Status = ACTIVE,
  @JsonProperty(readOnly = true) val customerName: String? = null,
  override val id: Id<Project> = Id()
): Entity<Project>, UpdatableEntity {
  enum class Status {
    ACTIVE,
    DELETED
  }
}
