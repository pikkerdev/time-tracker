package invoices

import db.Id
import klite.Converter
import klite.Decimal
import klite.jdbc.BaseEntity
import projects.Project
import java.time.LocalDate

data class Invoice(
  override val id: InvoiceId,
  val projectId: Id<Project>,
  val date: LocalDate,
  val amount: Decimal,
  val vatAmount: Decimal,
  val description: String,
  val dueDate: LocalDate,
): BaseEntity<InvoiceId> {
  val totalAmount: Decimal get() = amount + vatAmount
}

data class InvoiceView(
  val invoice: Invoice,
  val creatorName: String,
  val customerName: String,
  val projectName: String,
)

@JvmInline value class InvoiceId(val value: Long) {
  companion object {
    init {
      Converter.use { InvoiceId(it.toLong()) }
    }
  }
}
