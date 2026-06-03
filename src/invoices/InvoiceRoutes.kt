package invoices

import app.vatRate
import auth.Access
import db.Id
import klite.Decimal
import klite.annotations.POST
import klite.annotations.PathParam
import project.Project
import project.TimeEntry
import project.TimeEntryRepository
import users.AuthRole

class InvoiceRoutes(
  val repository: InvoiceRepository,
  val timeEntryRepository: TimeEntryRepository,
) {

 @POST @Access(AuthRole.ADMIN)
  fun create(@PathParam projectId: Id<Project>, timeEntryIds: List<Id<TimeEntry>>, invoice: Invoice) : InvoiceCreateRequest {
   val timeEntries = timeEntryRepository.listByIds(timeEntryIds)
   var netAmount = Decimal.ZERO
   for (timeEntry in timeEntries) { netAmount += timeEntry.hours * timeEntry.hourlyRate }
   val vatAmount = netAmount * Decimal(vatRate.toString())

   val newInvoiceId = repository.nextId(invoice.date)
   val invoiceToSave = invoice.copy(newInvoiceId, projectId, amount = netAmount, vatAmount = vatAmount)
   repository.save(invoiceToSave)
   timeEntryRepository.updateInvoiceId(timeEntryIds, newInvoiceId)
   return InvoiceCreateRequest(invoiceToSave, timeEntryIds)
 }

  data class InvoiceCreateRequest(val invoice: Invoice, val timeEntryIds: List<Id<TimeEntry>>)

}
