package invoices

import klite.jdbc.BaseCrudRepository
import java.time.LocalDate
import javax.sql.DataSource

class InvoiceRepository(db: DataSource): BaseCrudRepository<Invoice, InvoiceId>(db, "invoices") {
  fun nextId(date: LocalDate) = InvoiceId(
    date.year * 1000000L + date.monthValue * 10000L + date.dayOfMonth * 100L +
      (list(Invoice::date to date).firstOrNull()?.id?.value?.mod(100) ?: 0) + 1)
}
