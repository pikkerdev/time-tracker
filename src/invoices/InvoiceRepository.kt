package invoices

import db.Id
import invoices.Invoice.Status
import klite.Decimal
import klite.jdbc.BaseCrudRepository
import klite.jdbc.delete
import klite.jdbc.eq
import klite.jdbc.getLocalDate
import klite.jdbc.query
import klite.jdbc.select
import klite.jdbc.update
import klite.notNullValues
import projects.Project
import projects.MonthlyStats
import java.sql.ResultSet
import java.time.LocalDate
import javax.sql.DataSource

class InvoiceRepository(db: DataSource): BaseCrudRepository<Invoice, InvoiceId>(db, "invoices") {
  private val viewFrom = "$table join projects p on projectId = p.id join users u on createdBy = u.id join customers c on p.customerid = c.id"
  private val withCustomer = "$table i join projects p on i.projectId = p.id join users u on i.createdBy = u.id join customers c on p.customerid = c.id"
  override val defaultOrder = "order by invoices.id desc"

  fun nextId(date: LocalDate) = InvoiceId(
    date.year * 1000000L + date.monthValue * 10000L + date.dayOfMonth * 100L +
      (list(Invoice::date to date).firstOrNull()?.id?.value?.mod(100) ?: 0) + 1
  )

  fun getWithIds(id: InvoiceId): InvoiceWithIds =
    db.select(withCustomer, "i.id" eq id) {
      InvoiceWithIds(mapper(), Id(getLong("c.id")), Id(getLong("u.id")))
    }.first()

  fun listView(projectId: Id<Project>?) =
    db.select(viewFrom, notNullValues(Invoice::projectId eq projectId), suffix = defaultOrder) { viewMapper() }

  private fun ResultSet.viewMapper() =
    InvoiceView(
      mapper(),
      "${getString("u.firstname")} ${getString("u.lastname")}",
      getString("c.name"),
      getString("p.name")
    )

  fun delete(id: InvoiceId): Boolean {
    return if (db.delete(table, Invoice::id eq id) == 1) true else throw NoSuchElementException()
  }

  fun setStatus(id: InvoiceId, status: Status): Boolean{
    return if (db.update(table, mapOf(Invoice::status to status), Invoice::id to id) == 1) true else throw NoSuchElementException()
  }

  fun statsForProject(
    projectId: Id<Project>,
  ): Map<LocalDate, MonthlyStats> =
    db.query(
      """select
      coalesce(revenueMonth, date_trunc('month', date)::date) as month,
      coalesce(sum(invoice.hours), 0) as billedHours,
      coalesce(sum(invoice.total), 0) as billedRevenue
      from $table,
      lateral (
        select
          coalesce(sum((r->>'hours')::numeric), 0) as hours,
          coalesce(sum((r->>'amount')::numeric), 0) as total
        from jsonb_array_elements(rows) r
      ) invoice""".trimIndent(),
      Invoice::projectId eq projectId,
      suffix = "group by month order by month"
    ) {
      getLocalDate("month") to MonthlyStats(
        billedHours = Decimal(getString("billedHours")),
        unbilledHours = Decimal("0"),
        billedRevenue = Decimal(getString("billedRevenue")),
        unbilledRevenue = Decimal("0")
      )
    }.toMap()
}
