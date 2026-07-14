package invoices

import app.vatRate
import customers.Customer
import db.Id
import invoices.Invoice.Status.CREATED
import klite.Converter
import klite.Decimal
import klite.jdbc.BaseEntity
import klite.jdbc.UpdatableEntity
import projects.Project
import projects.ProjectMember.Role
import java.time.Instant
import java.time.LocalDate

data class Invoice(
  override val id: InvoiceId,
  val projectId: Id<Project>,
  val date: LocalDate,
  val amount: Decimal,
  val vatAmount: Decimal,
  val description: String,
  val dueDate: LocalDate,
  val status: Status = CREATED,
  override var updatedAt: Instant? = null,
): BaseEntity<InvoiceId>, UpdatableEntity {
  val totalAmount: Decimal get() = amount + vatAmount

  enum class Status {
    CREATED, SENT, PAID
  }
}

data class InvoiceView(
  val invoice: Invoice,
  val creatorName: String,
  val customerName: String,
  val projectName: String,
)

data class InvoiceWithCustomer(
  val invoice: Invoice,
  val customerId: Id<Customer>,
)

data class RoleHoursEntry(
  val role: Role,
  val hours: Decimal,
  val rate: Decimal,
)

data class InvoiceDetails(
  val invoice: Invoice,
  val customer: Customer,
  val entries: List<RoleHoursEntry>,
  val vat: Decimal = vatRate
)

@JvmInline value class InvoiceId(val value: Long) {
  companion object {
    init {
      Converter.use { InvoiceId(it.toLong()) }
    }
  }
}
