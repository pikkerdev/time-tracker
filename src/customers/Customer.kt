package customers

import db.Entity
import db.Id
import klite.Email
import klite.Phone

data class Customer(
  val name: String,
  val legalName: String? = null,
  val businessRegistryCode: String? = null,
  val legalAddress: String? = null,
  val vatId: String? = null,
  val invoiceEmail: Email? = null,
  val phone: Phone? = null,
  override val id: Id<Customer> = Id()
): Entity<Customer>
