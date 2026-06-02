package invoices

import db.Id
import klite.Decimal
import klite.jdbc.BaseEntity
import project.Project
import java.time.LocalDate

data class Invoice(
  override val id: InvoiceId,
  val projectId: Id<Project>,
  val date: LocalDate,
  val amount: Decimal,
  val vatAmount: Decimal,
): BaseEntity<InvoiceId> {
  val totalAmount: Decimal get() = amount + vatAmount
}

@JvmInline value class InvoiceId(val value: Long)
