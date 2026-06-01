package invoice

import customers.Customer
import db.Entity
import db.Id
import klite.Decimal
import project.Project
import project.TimeEntry
import java.time.LocalDate

data class Invoice(
  val projectId: Id<Project>,
  val customerId: Id<Customer>,
  val timeEntryIds: List<Id<TimeEntry>>,
  val number: String,
  val amount: Decimal,
  val vatAMount: Decimal,
  val totalAmount: Decimal,
  val createdAt: LocalDate = LocalDate.now(),
  override val id: Id<Invoice> = Id()
): Entity<Invoice>
