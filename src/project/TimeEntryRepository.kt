package project

import db.CrudRepository
import db.Id
import klite.jdbc.eq
import users.User
import java.time.LocalDate
import javax.sql.DataSource

class TimeEntryRepository(db: DataSource): CrudRepository<TimeEntry>(db, "time_entry") {

  fun byUser(userId: Id<User>): List<TimeEntry> =
    list( TimeEntry::userId eq userId)

  fun byUserAndDate(userId: Id<User>, date: LocalDate): List <TimeEntry> =
    list(TimeEntry:: userId eq userId, TimeEntry:: date eq date)

}
