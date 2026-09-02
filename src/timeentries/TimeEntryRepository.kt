package timeentries

import db.CrudRepository
import db.Id
import db.Status.ACTIVE
import invoices.InvoiceId
import invoices.InvoiceRow
import invoices.InvoiceRow.Type.TIMEENTRY
import klite.Decimal
import klite.d
import klite.jdbc.*
import klite.notNullValues
import projects.MonthlyStats
import projects.Project
import users.User
import java.sql.ResultSet
import java.time.LocalDate
import javax.sql.DataSource

class TimeEntryRepository(db: DataSource): CrudRepository<TimeEntry>(db, "time_entry") {
  private val viewFrom =
    "$table join projects p on projectId = p.id join users u on userId = u.id join customers c on p.customerId = c.id"

  override val defaultOrder = "order by date desc, $table.id desc"

  fun listView(
    userId: Id<User>? = null,
    projectId: Id<Project>? = null,
    from: LocalDate? = null,
    to: LocalDate? = null
  ): List<TimeEntryView> {
    val queryFrom = from ?: to
    val queryTo = to ?: from
    return db.select(
      viewFrom, notNullValues(
        TimeEntry::userId eq userId,
        TimeEntry::projectId eq projectId,
        TimeEntry::date gte queryFrom,
        TimeEntry::date lte queryTo,
        "p.status" eq ACTIVE
      ), suffix = defaultOrder
    ) { viewMapper() }
  }

  private fun ResultSet.viewMapper() =
    TimeEntryView(
      mapper(),
      getString("c.name"),
      getString("p.name"),
      getString("u.firstName") + " " + getString("u.lastName")
    )

  fun listViewForCustomer(
    userId: Id<User>,
    projectId: Id<Project>? = null,
    from: LocalDate? = null,
    to: LocalDate? = null
  ): List<TimeEntryView> {
    val queryFrom = from ?: to
    val queryTo = to ?: from
    return db.select(
      "$viewFrom join project_members pm on p.id = pm.projectId", notNullValues(
        projectId?.let { "time_entry.projectId" eq it },
        TimeEntry::date gte queryFrom,
        TimeEntry::date lte queryTo,
        "pm.userId" eq userId,
        "p.status" eq ACTIVE
      ), suffix = defaultOrder
    ) { viewMapper() }
  }

  fun userTimes(
    userId: Id<User>,
    from: LocalDate = LocalDate.now().minusDays(30),
    until: LocalDate = LocalDate.now()
  ): Map<LocalDate, Map<String, Decimal>> =
    db.query(
      "select date, color, sum(hours) as hours from $table join projects p on projectId = p.id",
      TimeEntry::userId to userId, TimeEntry::date to Between(from, until), "p.status" eq ACTIVE, suffix = "group by date, color"
    ){
      Triple(getLocalDate("date"), getString("color"), Decimal(getString("hours")))
    }
      .groupBy({ it.first }, { it.second to it.third })
      .mapValues { (_, value) -> value.toMap() }

  fun listByIds(ids: List<Id<TimeEntry>>): List<TimeEntry> =
    list(TimeEntry::id to ids)

  fun listViewByIds(ids: List<Id<TimeEntry>>): List<TimeEntryView> =
    db.select(
      viewFrom, notNullValues("time_entry.id" eq ids,), suffix = defaultOrder
    ) { viewMapper() }

  fun initialRows(ids: List<Id<TimeEntry>>): List<InvoiceRow> =
    db.query(
      "select role, sum(hours * hourlyRate) as amount, sum(hours) as hours, hourlyRate from $table",
      TimeEntry::id eq ids, suffix = "group by role, hourlyRate order by role"
    ) {
      InvoiceRow(getString("role"), Decimal(getString("amount")), Decimal(getString("hours")), Decimal(getString("hourlyRate")), TIMEENTRY)
    }

  fun invoiceRows(invoiceId: InvoiceId): List<InvoiceRow> =
    initialRows(list(TimeEntry::invoiceId to invoiceId).map { it.id })

  fun updateInvoiceId(ids: List<Id<TimeEntry>>, invoiceId: InvoiceId) {
    db.update(table, mapOf(TimeEntry::invoiceId to invoiceId), TimeEntry::id to ids)
  }

  fun statsForProject(
    projectId: Id<Project>,
  ): Map<LocalDate, MonthlyStats> =
    db.query(
      """select
      date_trunc('month', date)::date as month,
      coalesce(sum(case when invoiceId is null then hours else 0 end), 0) as unbilledHours,
      coalesce(sum(case when invoiceId is null then hours * hourlyRate else 0 end), 0) as unbilledRevenue
      from $table""".trimIndent(),
      TimeEntry::projectId eq projectId,
      suffix = "group by month order by month"
    ) {
      getLocalDate("month") to MonthlyStats(
        billedHours = 0.d,
        unbilledHours = Decimal(getString("unbilledHours")),
        billedRevenue = 0.d,
        unbilledRevenue = Decimal(getString("unbilledRevenue"))
      )
    }.toMap()

  fun delete(id: Id<TimeEntry>) =
    db.delete(table, TimeEntry::id to id, TimeEntry::invoiceId to null)

  fun updateHourlyRates(ids: List<Id<TimeEntry>>, rate: Decimal) {
    db.update(table, mapOf(TimeEntry::hourlyRate to rate), TimeEntry::id to ids)
  }
}
