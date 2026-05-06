package project

import customers.Customer
import db.Entity
import db.Id
import java.math.BigDecimal

data class Project(
  override val id: Id<Project> = Id(),
  val customerId: Id<Customer>,
  val name: String,
  val description: String? = null,
  val currency: String = "EUR",
  val hourlyRate: BigDecimal,
  val storyTrackerId: Int? = null,
): Entity<Project>

data class ProjectWithCustomer(
  val project: Project,
  val customer: Customer
) {
  val id get() = customer.id
  val customerName get() = customer.name
  val name get() = project.name
  val storyTrackerId get() = project.storyTrackerId

}
