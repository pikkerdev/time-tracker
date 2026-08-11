package invoices

import app.vatRate
import customers.Customer
import db.Id
import invoices.Invoice.Status.CREATED
import klite.Converter
import klite.Decimal
import klite.jdbc.BaseEntity
import klite.jdbc.JsonColumn
import klite.jdbc.UpdatableEntity
import projects.Project
import users.User
import java.time.Instant
import java.time.LocalDate

data class Invoice(
  override val id: InvoiceId,
  val projectId: Id<Project>,
  val date: LocalDate,
  val title: String,
  val description: String,
  val dueDate: LocalDate,
  val revenueMonth: LocalDate,
  @JsonColumn val rows: List<InvoiceRow> = emptyList(),
  val status: Status = CREATED,
  override var updatedAt: Instant? = null,
): BaseEntity<InvoiceId>, UpdatableEntity {
  enum class Status {
    CREATED, SENT, PAID
  }
}

data class InvoiceRow(
  val description: String,
  val amount: Decimal,
  val hours: Decimal? = null,
  val rate: Decimal? = null
) {
  init {
    if (hours != null && rate != null) {
      require(hours * rate == amount) {
        "Amount does not match hours and rate"
      }
    }
  }
}

data class InvoiceView(
  val invoice: Invoice,
  val creatorName: String,
  val customerName: String,
  val projectName: String,
)

data class InvoiceWithIds(
  val invoice: Invoice,
  val customerId: Id<Customer>,
  val creatorId: Id<User>
)

data class InvoiceDetails(
  val invoice: Invoice,
  val customer: Customer,
  val creator: User,
  val vat: Decimal = vatRate
)

@JvmInline value class InvoiceId(val value: Long) {
  companion object {
    init {
      Converter.use { InvoiceId(it.toLong()) }
    }
  }
}
