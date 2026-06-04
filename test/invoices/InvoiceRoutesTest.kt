package invoices

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import db.BaseMocks
import db.TestData.invoice
import db.TestData.project
import db.TestData.timeEntry
import db.TestData.timeEntry2
import db.TestData.invoiceCreateRequest
import io.mockk.every
import io.mockk.verify
import klite.Decimal
import org.junit.jupiter.api.Test

class InvoiceRoutesTest: BaseMocks() {
  val routes = create<InvoiceRoutes>()

  @Test fun create() {

    val timeEntryIds = listOf(timeEntry.id, timeEntry2.id)
    val amount = (timeEntry.hours * timeEntry.hourlyRate) + (timeEntry2.hours * timeEntry2.hourlyRate)
    val vat = (amount * Decimal(app.vatRate.toString()))
    val invoiceToSave = invoice.copy( amount = amount, vatAmount = vat)

    every { timeEntryRepository.listByIds(timeEntryIds) } returns listOf(timeEntry, timeEntry2)

    val result = routes.create(project.id, timeEntryIds, invoice)

    expect(result.invoice).toEqual(invoiceToSave)
    expect(result.timeEntryIds).toEqual(timeEntryIds)
    expect(result).toEqual(invoiceCreateRequest.copy(invoice = invoiceToSave))

    verify {
      timeEntryRepository.listByIds(timeEntryIds)
      invoiceRepository.nextId(invoice.date)
      invoiceRepository.save(invoiceToSave)
      timeEntryRepository.updateInvoiceId(timeEntryIds, invoice.id)
    }
  }
}
