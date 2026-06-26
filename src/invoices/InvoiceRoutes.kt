package invoices

import app.vatRate
import auth.Access
import db.Id
import klite.annotations.GET
import klite.annotations.POST
import klite.annotations.QueryParam
import klite.sumOf
import projects.Project
import timeentries.TimeEntry
import timeentries.TimeEntryRepository
import users.AuthRole.ADMIN
import java.time.LocalDate

@Access(ADMIN)
class InvoiceRoutes(
  val repository: InvoiceRepository,
  val timeEntryRepository: TimeEntryRepository
) {
  @GET fun get(@QueryParam projectId: Id<Project>?) = repository.listView(projectId)

  @POST fun create(req: InvoiceCreateRequest): Invoice {
    val timeEntries = timeEntryRepository.listByIds(req.timeEntryIds)
    val projectId = timeEntries.first().projectId
    if (timeEntries.any { it.projectId != projectId }) throw IllegalArgumentException("timeEntries.timeEntriesMustBelongToSameProject")
    require(timeEntries.all { it.invoiceId == null }) { "Already Invoiced" }
    val netAmount = timeEntries.sumOf { it.hours * it.hourlyRate }
    val vatAmount = netAmount * vatRate
    val invoice = Invoice(repository.nextId(req.date), projectId, req.date, netAmount, vatAmount, req.description, req.dueDate)
    repository.save(invoice)
    timeEntryRepository.updateInvoiceId(req.timeEntryIds, invoice.id)
    return invoice
  }
}

data class InvoiceCreateRequest(val date: LocalDate, val timeEntryIds: List<Id<TimeEntry>>, val description: String, val dueDate: LocalDate)

