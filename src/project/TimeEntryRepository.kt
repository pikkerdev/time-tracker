package project

import db.CrudRepository
import db.Id
import klite.Decimal
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

  fun listView(userId: Id<User>? = null, date: LocalDate? = null): List<TimeEntryView> =
    db.select(viewFrom, notNullValues(TimeEntry::userId eq userId, TimeEntry::date eq date)) { viewMapper() }

  private fun ResultSet.viewMapper() = TimeEntryView(
    entry = mapper(),
    customerName = getString("c.name"),
    projectName = getString("p.name"),
    userName = getString("u.firstName") + " " + getString("u.lastName")
  )

  fun userTimes(userId: Id<User>, from: LocalDate? = LocalDate.now().minusDays(30), singleDay: LocalDate? = null): Map<LocalDate, Decimal> {
    val queryArgs = mutableListOf<Pair<Any, Any?>>(TimeEntry::userId to userId)
    if (singleDay != null) queryArgs.add(TimeEntry::date to singleDay)
    return db.query(
      "select date, sum(hours) as total_hours from $table",
      *queryArgs.toTypedArray(),
      suffix = if (singleDay == null && from != null) "and date >= '$from' group by date" else "group by date")
    { getLocalDate("date") to Decimal(getString("total_hours")) }.toMap()
  }
}
