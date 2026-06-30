package timeentries

import db.CrudRepository
import db.Id
import invoices.InvoiceId
import invoices.RoleHoursEntry
import klite.Decimal
import klite.jdbc.*
import klite.notNullValues
import projects.Project
import projects.ProjectMember.Role
import projects.ProjectStats
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
        Project::status eq Project.Status.ACTIVE
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

  fun userTimes(
    userId: Id<User>,
    from: LocalDate = LocalDate.now().minusDays(30),
    until: LocalDate = LocalDate.now()
  ): Map<LocalDate, Map<String, Decimal>> =
    db.query(
      "select date, color, sum(hours) as hours from $table join projects p on projectId = p.id",
      TimeEntry::userId to userId, TimeEntry::date to Between(from, until), suffix = "group by date, color"
    ){
      Triple(getLocalDate("date"), getString("color"), Decimal(getString("hours")))
    }
      .groupBy({ it.first }, { it.second to it.third })
      .mapValues { (_, value) -> value.toMap() }

  fun listByIds(ids: List<Id<TimeEntry>>): List<TimeEntry> =
    list(TimeEntry::id to ids)

  fun sumHoursByRoleForInvoice(id: InvoiceId): List<RoleHoursEntry> =
    db.query(
      "select role, sum(hours) as hours, hourlyRate from $table",
      TimeEntry::invoiceId eq id, suffix = "group by role, hourlyRate order by role"
    ) {
      RoleHoursEntry(Role.valueOf(getString("role")), Decimal(getString("hours")), Decimal(getString("hourlyRate")))
    }

  fun updateInvoiceId(ids: List<Id<TimeEntry>>, invoiceId: InvoiceId) {
    db.update(table, mapOf(TimeEntry::invoiceId to invoiceId), TimeEntry::id to ids)
  }

  fun statsForProject(id: Id<Project>) =
    db.query<ProjectStats>("""select
      coalesce(sum(hours), 0) as totalHours,
      coalesce(sum(case when invoiceId is null then hours else 0 end), 0) as unbilledHours,
      coalesce(sum(hours * hourlyRate), 0) as totalRevenue,
      coalesce(sum(case when invoiceId is null then hours * hourlyRate else 0 end), 0) as unbilledRevenue
      from $table""".trimIndent(),
      TimeEntry::projectId eq id
    ).first()
}
