package invoice

import db.CrudRepository
import java.time.LocalDate
import javax.sql.DataSource
import klite.jdbc.eq
import klite.jdbc.select

class InvoiceRepository(db: DataSource): CrudRepository<Invoice>(db, "invoices") {

}

