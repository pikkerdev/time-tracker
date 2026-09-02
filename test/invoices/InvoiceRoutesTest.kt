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
import db.TestData.invoiceRow
import db.TestData.invoiceView
import db.TestData.invoiceWithIds
import db.TestData.timeEntry
import db.TestData.timeEntry2
import db.TestData.user
import invoices.Invoice.Status.*
import io.mockk.every
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.jupiter.api.Test


class InvoiceRoutesTest: BaseMocks() {
  val routes = create<InvoiceRoutes>()

  @Test fun get() {
    every { invoiceRepository.listView(null) } returns listOf(invoiceView)

    expect(routes.get(null)).toEqual(listOf(invoiceView))

    verify { invoiceRepository.listView(null) }
  }

  @Test fun getDetails() {
    every { invoiceRepository.getWithIds(invoice.id) } returns invoiceWithIds
    every { customerRepository.get(customer.id) } returns customer
    every { userRepository.get(user.id) } returns user
    expect(routes.getDetails(invoice.id)).toEqual(invoiceDetails)

    verify {
      invoiceRepository.getWithIds(invoice.id)
      customerRepository.get(customer.id)
      userRepository.get(user.id)
    }
  }

  @Test fun create() {
    val timeEntryIds = listOf(timeEntry.id, timeEntry2.id)
    val invoiceToSave = invoice.copy()

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

  @Test fun update() {
    val request = InvoiceUpdateRequest(
      invoice.id,
      invoice.date.plusDays(1),
      "Updated title",
      "Updated description",
      invoice.dueDate.plusDays(1),
      invoice.revenueMonth.plusMonths(1),
      invoice.rows
    )
    val updated = invoice.copy(
      date = request.date,
      title = request.title,
      description = request.description,
      dueDate = request.dueDate,
      revenueMonth = request.revenueMonth,
      rows = request.rows
    )

    expect(routes.update(invoice.id, request)).toEqual(updated)

    verify { invoiceRepository.get(invoice.id); invoiceRepository.save(updated) }
  }

  @Test fun `update does not remove time entry rows`() {
    every { timeEntryRepository.invoiceRows(invoice.id) } returns listOf(invoiceRow)
    val request = InvoiceUpdateRequest(invoice.id, invoice.date, invoice.title, invoice.description, invoice.dueDate, invoice.revenueMonth, emptyList())

    expect { routes.update(invoice.id, request) }.toThrow<IllegalArgumentException>()
  }

  @Test fun setStatus() {
    routes.setStatus(invoice.id, CREATED)
    routes.setStatus(invoice.id, SENT)
    routes.setStatus(invoice.id, PAID)

    verifySequence {
      invoiceRepository.setStatus(invoice.id, CREATED)
      invoiceRepository.setStatus(invoice.id, SENT)
      invoiceRepository.setStatus(invoice.id, PAID)
    }

    every { invoiceRepository.setStatus(invoice.id, SENT) } returns true

    expect(routes.setStatus(invoice.id, SENT)).toEqual(true)
  }
}
