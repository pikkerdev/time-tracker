package project

import db.CrudRepository
import db.Id
import klite.jdbc.eq
import klite.jdbc.select
import users.User
import javax.sql.DataSource

class TimeEntryRepository(db: DataSource): CrudRepository<TimeEntry>(db, "time_entry") {

  fun forUser(userId: Id<User>): List<TimeEntry> =
    list( TimeEntry::userId eq userId)


}
