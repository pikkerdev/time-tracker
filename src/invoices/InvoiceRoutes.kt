package invoices

import app.vatRate
import auth.Access
import db.Id
import klite.sumOf
import klite.annotations.POST
import klite.annotations.PathParam
import project.Project
import project.TimeEntry
import project.TimeEntryRepository
import users.AuthRole.ADMIN
import java.time.LocalDate

class InvoiceRoutes(
  val repository: InvoiceRepository,
  val timeEntryRepository: TimeEntryRepository
) {

 @POST @Access(ADMIN)
 fun create(@PathParam projectId: Id<Project>, req: InvoiceCreateRequest): Invoice {
   val timeEntries = timeEntryRepository.listByIds(req.timeEntryIds)
   val netAmount = timeEntries.sumOf { it.hours * it.hourlyRate }
   val vatAmount = netAmount * vatRate

   val invoice = Invoice(repository.nextId(req.date), projectId, req.date, netAmount, vatAmount)
   repository.save(invoice)
   timeEntryRepository.updateInvoiceId(req.timeEntryIds, invoice.id)
   return invoice
 }

  data class InvoiceCreateRequest(val date: LocalDate, val timeEntryIds: List<Id<TimeEntry>>)
}
