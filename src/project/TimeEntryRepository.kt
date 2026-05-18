package project

import db.CrudRepository
import db.Id
import klite.jdbc.create
import klite.jdbc.eq
import klite.jdbc.select
import users.User
import java.sql.ResultSet
import java.time.LocalDate
import javax.sql.DataSource

class TimeEntryRepository(db: DataSource): CrudRepository<TimeEntry>(db, "time_entry") {

  private val viewFrom = "$table join projects p on $table.projectId = p.id join users u on $table.userId = u.id join customers c on p.customerId = c.id"

  fun listView(): List<TimeEntryView> =
    db.select(viewFrom) { viewTo() }

  fun listViewByUser(userId: Id<User>): List<TimeEntryView> =
    db.select(viewFrom, TimeEntry::userId eq userId) { viewTo() }

  fun listViewByUserAndDate(userId: Id<User>, date: LocalDate): List<TimeEntryView> =
    db.select(viewFrom, TimeEntry::userId eq userId, TimeEntry:: date eq date) { viewTo() }

  private fun ResultSet.viewTo() = TimeEntryView(
    timeEntry = create(),
    projectName = getString("p.name"),
    userName = getString("u.firstName") + " " + getString("u.lastName"),
    customerName = getString("c.name")
  )

}
