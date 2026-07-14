package invoices

import app.vatRate
import auth.Access
import customers.CustomerRepository
import db.Id
import invoices.Invoice.Status
import invoices.Invoice.Status.CREATED
import klite.ForbiddenException
import klite.annotations.*
import klite.sumOf
import projects.Project
import timeentries.TimeEntry
import timeentries.TimeEntryRepository
import users.AuthRole.ADMIN
import java.time.LocalDate

@Access(ADMIN)
class InvoiceRoutes(
  val repository: InvoiceRepository,
  val customerRepository: CustomerRepository,
  val timeEntryRepository: TimeEntryRepository
) {
  @GET fun get(@QueryParam projectId: Id<Project>?) = repository.listView(projectId)

  @GET("/:id") fun getDetails(@PathParam id: InvoiceId): InvoiceDetails {
    val withCustomer = repository.getWithCustomerId(id)
    val customer = customerRepository.get(withCustomer.customerId)
    val sumHoursByRole = timeEntryRepository.sumHoursByRoleForInvoice(id)
    return InvoiceDetails(withCustomer.invoice, customer,sumHoursByRole)
  }

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

  @DELETE("/:id") fun delete(@PathParam id: InvoiceId) =
    if (repository.get(id).status == CREATED) repository.delete(id)
    else throw ForbiddenException()

  @POST("/:id")
  fun setStatus(@PathParam id: InvoiceId, status: Status) = repository.setStatus(id, status)
}

data class InvoiceCreateRequest(val date: LocalDate, val timeEntryIds: List<Id<TimeEntry>>, val description: String, val dueDate: LocalDate)

