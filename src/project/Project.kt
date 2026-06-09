package project

import customers.Customer
import db.Entity
import db.Id
import klite.Decimal
import klite.json.JsonProperty
import java.util.Currency

val EUR: Currency = Currency.getInstance("EUR")

data class Project(
  val customerId: Id<Customer>,
  val name: String,
  val description: String? = null,
  val hourlyRates: Map<ProjectMember.Role, Decimal>,
  val currency: Currency = EUR,
  val storyTrackerId: Long? = null,
  val tags: Set<String> = setOf("Development", "Meeting", "Consultancy", "Testing", "Research", "Support"),
  @JsonProperty(readOnly = true) val customerName: String? = null,
  override val id: Id<Project> = Id()
): Entity<Project>
