package merit

import klite.Config
import klite.Email
import klite.Phone
import locations.CountryCode
import projects.EUR
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

data class MeritInvoice(
  val customer: Customer,
  val invoiceNo: String,
  val invoiceRow: List<Row>,
  val taxAmount: List<TaxAmount>,
  val totalAmount: BigDecimal = invoiceRow.sumOf { it.amount },
  val currencyCode: Currency = EUR,
  val docDate: LocalDate = LocalDate.now(),
  val dueDate: LocalDate = LocalDate.now().plusDays(60),
  val fComment: String = "",
  val transactionDate: LocalDate = LocalDate.now(),
) {
  companion object {
    val defaultTaxId = Config.optional("MERIT_TAX_ID", "b9b25735-6a15-4d4e-8720-25b254ae3d21")
  }

  data class Customer(
    val name: String,
    val notTDCustomer: Boolean,
    val countryCode: CountryCode,
    val phoneNo: Phone? = null,
    val email: Email? = null,
    val regNo: String? = null
  )

  data class Row(
    val item: Item,
    val price: BigDecimal,
    val quantity: Int = 1,
    val taxId: String = defaultTaxId,
  ) {
    val amount get() = price * quantity.toBigDecimal()
  }

  data class Item(
    val code: String,
    val description: String,
    val type: Int = 2, // types	1 = stock item, 2 = service, 3 = item
  )

  data class TaxAmount(val amount: BigDecimal? = null, val taxId: String = defaultTaxId)

  data class Response(
    val customerId: UUID,
    val invoiceId: UUID,
    val invoiceNo: String,
    val refNo: Int
  )

  data class InvoiceRequest(val id: UUID, val addAttachment: Boolean = false)
  data class InvoiceResponse(val header: InvoiceResponseHeader, val payments: List<Payment> = emptyList())
  data class InvoiceResponseHeader(val paid: Boolean)
  data class Payment(val paymDate: LocalDate, val amount: BigDecimal)
}
