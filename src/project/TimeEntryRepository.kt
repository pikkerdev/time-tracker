package project

import db.CrudRepository
import javax.sql.DataSource

class TimeEntryRepository(db: DataSource): CrudRepository<TimeEntry>(db, "time_entries") {
}
