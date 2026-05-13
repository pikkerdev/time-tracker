package project

import customers.Customer
import db.Entity
import db.Id
import klite.Decimal
import klite.json.JsonProperty
import java.math.BigDecimal

data class Project(
  override val id: Id<Project> = Id(),
  val customerId: Id<Customer>,
  val name: String,
  val description: String? = null,
  val currency: String = "EUR",
  val hourlyRates: Map<ProjectMember.Role, Decimal>,
  val storyTrackerId: Int? = null,
  @JsonProperty(readOnly = true) val customerName: String? = null
): Entity<Project>
