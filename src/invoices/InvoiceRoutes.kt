package invoices

import auth.Access
import customers.CustomerRepository
import db.Id
import invoices.Invoice.Status
import invoices.Invoice.Status.PAID
import invoices.Invoice.Status.SENT
import klite.Decimal
import klite.ForbiddenException
import klite.annotations.*
import klite.d
import projects.Project
import projects.ProjectRepository
import timeentries.TimeEntry
import timeentries.TimeEntryRepository
import users.AuthRole.ADMIN
import users.UserRepository
import java.time.LocalDate

@Access(ADMIN)
class InvoiceRoutes(
  val repository: InvoiceRepository,
  val customerRepository: CustomerRepository,
  val projectRepository: ProjectRepository,
  val timeEntryRepository: TimeEntryRepository,
  val userRepository: UserRepository
) {
  @GET fun get(@QueryParam projectId: Id<Project>?, @QueryParam showPaid: Boolean?) = repository.listView(projectId, showPaid ?: false)

  @GET("/:id") fun getDetails(@PathParam id: InvoiceId): InvoiceDetails {
    val withCustomer = repository.getWithIds(id)
    val customer = customerRepository.get(withCustomer.customerId)
    val user = userRepository.get(withCustomer.creatorId)
    val invoice = withCustomer.invoice.copy(rows = withCustomer.invoice.rows.filter { it.amount > 0.d })
    return InvoiceDetails(invoice, customer, user, projectRepository.get(invoice.projectId).name)
  }

  @POST fun create(req: InvoiceCreateRequest): Invoice {
    val timeEntries = timeEntryRepository.listByIds(req.timeEntryIds)
    val projectId = timeEntries.first().projectId
    if (timeEntries.any { it.projectId != projectId }) throw IllegalArgumentException("timeEntries.timeEntriesMustBelongToSameProject")
    require(timeEntries.all { it.invoiceId == null }) { "Already Invoiced" }
    val initialRows = timeEntryRepository.initialRows(req.timeEntryIds)
    val invoice = Invoice(repository.nextId(req.date), projectId, req.date, req.title, req.description, req.dueDate, req.revenueMonth, req.rows + initialRows)
    repository.save(invoice)
    timeEntryRepository.updateInvoiceId(req.timeEntryIds, invoice.id)
    return invoice
  }

  @POST("/:id")
  fun update(@PathParam id: InvoiceId, req: InvoiceUpdateRequest): Invoice {
    require(id == req.id) { "Wrong id" }
    val lockedRows = timeEntryRepository.invoiceRows(id)
    require(lockedRows.all { row -> req.rows.count { it == row } >= lockedRows.count { it == row } }) {
      "Can not remove rows created from time entries"
    }
    val invoice = repository.get(id).copy(
      date = req.date,
      title = req.title,
      description = req.description,
      dueDate = req.dueDate,
      revenueMonth = req.revenueMonth,
      rows = req.rows
    )
    repository.save(invoice)
    return invoice
  }

  @DELETE("/:id") fun delete(@PathParam id: InvoiceId) =
    if (repository.get(id).status == PAID) throw ForbiddenException()
    else repository.delete(id)

  @POST("/:id/status")
  fun setStatus(@PathParam id: InvoiceId, status: Status): Boolean =
    if (status == PAID) {
      val invoice = repository.get(id)
      repository.setPayment(id, invoice.totalAmount(), PAID)
    } else repository.setStatus(id, status)

  @POST("/:id/paidAmount")
  fun setPaidAmount(@PathParam id: InvoiceId, paidAmount: Decimal): Invoice {
    require(paidAmount >= 0.d) { "Paid amount can not be negative" }
    val invoice = repository.get(id)
    val total = invoice.totalAmount()
    require(paidAmount <= total) { "Paid amount can not exceed total amount" }
    val status = when {
      paidAmount >= total -> PAID
      invoice.status == PAID -> SENT
      else -> invoice.status
    }
    if (status == invoice.status) repository.setPaidAmount(id, paidAmount)
    else repository.setPayment(id, paidAmount, status)
    return repository.get(id)
  }
}

data class InvoiceCreateRequest(val date: LocalDate, val timeEntryIds: List<Id<TimeEntry>>, val title: String, val description: String, val dueDate: LocalDate, val revenueMonth: LocalDate, val rows: List<InvoiceRow> )

data class InvoiceUpdateRequest(val id: InvoiceId, val date: LocalDate, val title: String, val description: String, val dueDate: LocalDate, val revenueMonth: LocalDate, val rows: List<InvoiceRow>)
