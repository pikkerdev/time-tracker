package customers

import db.Entity
import db.Id
import klite.Email
import klite.Phone
import klite.jdbc.UpdatableEntity
import db.Status
import db.Status.ACTIVE
import java.time.Instant

data class Customer(
  val name: String,
  val legalName: String? = null,
  val businessRegistryCode: String? = null,
  val legalAddress: String? = null,
  val vatId: String? = null,
  val invoiceEmail: Email? = null,
  val phone: Phone? = null,
  val status: Status = ACTIVE,
  override var updatedAt: Instant? = null,
  override val id: Id<Customer> = Id()
): Entity<Customer>, UpdatableEntity
