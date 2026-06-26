package invoices

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import db.BaseMocks
import db.Id
import db.TestData.customer
import db.TestData.invoice
import db.TestData.invoiceCreateRequest
import db.TestData.invoiceDetails
import db.TestData.invoiceView
import db.TestData.invoiceWithCustomer
import db.TestData.rolesHoursEntry
import db.TestData.timeEntry
import db.TestData.timeEntry2
import io.mockk.every
import io.mockk.verify
import klite.Decimal
import org.junit.jupiter.api.Test


class InvoiceRoutesTest: BaseMocks() {
  val routes = create<InvoiceRoutes>()

  @Test fun get() {
    every { invoiceRepository.listView(null) } returns listOf(invoiceView)

    expect(routes.get(null)).toEqual(listOf(invoiceView))

    verify { invoiceRepository.listView(null) }
  }

  @Test fun getDetails() {
    every { invoiceRepository.getWithCustomerId(invoice.id) } returns invoiceWithCustomer
    every { customerRepository.get(customer.id) } returns customer
    every { timeEntryRepository.sumHoursByRoleForInvoice(invoice.id) } returns listOf(rolesHoursEntry)

    expect(routes.getDetails(invoice.id)).toEqual(invoiceDetails)

    verify {
      invoiceRepository.getWithCustomerId(invoice.id)
      customerRepository.get(customer.id)
      timeEntryRepository.sumHoursByRoleForInvoice(invoice.id)
    }
  }

  @Test fun create() {
    val timeEntryIds = listOf(timeEntry.id, timeEntry2.id)
    val amount = (timeEntry.hours * timeEntry.hourlyRate) + (timeEntry2.hours * timeEntry2.hourlyRate)
    val vat = (amount * Decimal(app.vatRate.toString()))
    val invoiceToSave = invoice.copy(amount = amount, vatAmount = vat)

    every { timeEntryRepository.listByIds(timeEntryIds) } returns listOf(timeEntry, timeEntry2)

    val result = routes.create(invoiceCreateRequest)
    expect(result).toEqual(invoiceToSave)

    val differentProjectIdEntry = (timeEntry.copy(projectId = Id()))
    every { timeEntryRepository.listByIds(timeEntryIds) } returns listOf(timeEntry2, differentProjectIdEntry)
    expect { routes.create(invoiceCreateRequest.copy(timeEntryIds = listOf(differentProjectIdEntry.id, timeEntry2.id))) }.toThrow<IllegalArgumentException>()

    verify {
      timeEntryRepository.listByIds(timeEntryIds)
      invoiceRepository.nextId(invoice.date)
      invoiceRepository.save(invoiceToSave)
      timeEntryRepository.updateInvoiceId(timeEntryIds, invoice.id)
    }
  }

  @Test fun delete() {
    every { invoiceRepository.delete(invoice.id) } returns true

    expect(routes.delete(invoice.id)).toEqual(true)

    verify {
      invoiceRepository.delete(invoice.id)
    }
  }
}
