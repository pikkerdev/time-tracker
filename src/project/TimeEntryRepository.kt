package project

import db.CrudRepository
import db.Id
import klite.Decimal
import klite.jdbc.Between
import klite.jdbc.eq
import klite.jdbc.getLocalDate
import klite.jdbc.query
import klite.jdbc.select
import klite.notNullValues
import users.User
import java.sql.ResultSet
import java.time.LocalDate
import javax.sql.DataSource

class TimeEntryRepository(db: DataSource): CrudRepository<TimeEntry>(db, "time_entry") {
  private val viewFrom =
    "$table join projects p on projectId = p.id join users u on userId = u.id join customers c on p.customerId = c.id"

  override val defaultOrder = "order by $table.date desc, $table.id desc"

  fun listView(userId: Id<User>? = null, date: LocalDate? = null): List<TimeEntryView> =
    db.select(viewFrom, notNullValues(TimeEntry::userId eq userId, TimeEntry::date eq date), suffix = defaultOrder) { viewMapper() }

  private fun ResultSet.viewMapper() = TimeEntryView(
    entry = mapper(),
    customerName = getString("c.name"),
    projectName = getString("p.name"),
    userName = getString("u.firstName") + " " + getString("u.lastName")
  )

  fun userTimes(userId: Id<User>, from: LocalDate = LocalDate.now().minusDays(30), until: LocalDate = LocalDate.now()): Map<LocalDate, Decimal> =
    db.query(
      "select date, sum(hours) as total_hours from $table",
      TimeEntry::userId to userId, TimeEntry::date to Between(from, until),
      suffix = "group by date")
    { getLocalDate("date") to Decimal(getString("total_hours")) }.toMap()
}
