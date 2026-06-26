package invoices

import db.Id
import klite.jdbc.BaseCrudRepository
import klite.jdbc.eq
import klite.jdbc.select
import klite.notNullValues
import projects.Project
import java.sql.ResultSet
import java.time.LocalDate
import javax.sql.DataSource

class InvoiceRepository(db: DataSource): BaseCrudRepository<Invoice, InvoiceId>(db, "invoices") {
  private val viewFrom = "$table join projects p on projectId = p.id join customers c on p.customerid = c.id"
  override val defaultOrder = "order by date desc"

  fun nextId(date: LocalDate) = InvoiceId(
    date.year * 1000000L + date.monthValue * 10000L + date.dayOfMonth * 100L +
      (list(Invoice::date to date).firstOrNull()?.id?.value?.mod(100) ?: 0) + 1
  )

  fun listView(projectId: Id<Project>?) =
    db.select(viewFrom, notNullValues(Invoice::projectId eq projectId), suffix = defaultOrder) { viewMapper() }

  private fun ResultSet.viewMapper() =
    InvoiceView(
      mapper(),
      getString("c.name"),
      getString("p.name")
    )
}
