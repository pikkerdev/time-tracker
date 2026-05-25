package project

import customers.Customer
import db.CrudRepository
import db.Id
import db.json
import klite.Decimal
import klite.i18n.Lang.jsonMapper
import klite.jdbc.create
import klite.jdbc.eq
import klite.jdbc.get
import klite.jdbc.neq
import klite.jdbc.select
import klite.json.parse
import klite.toValues
import project.Status.DELETED
import users.User
import java.lang.reflect.Member
import java.math.BigDecimal
import java.sql.ResultSet
import javax.sql.DataSource

class ProjectRepository(db: DataSource): CrudRepository<Project>(db, "projects") {
  override val selectFrom = "$table join customers c on customerId = c.id"
  override val orderAsc = "order by $table.name"
  override val defaultOrder get() = orderAsc

  private val notDeleted = ProjectMember::status neq DELETED

  override fun ResultSet.mapper() = create(
    Project::hourlyRates to jsonMapper.parse<Map<ProjectMember.Role, Decimal>>(getString("hourlyRates")),
    Project::customerName to getString("c.name")
  )
  override fun Project.persister() = toValues(Project::hourlyRates to json(hourlyRates), skip = listOf(Project::customerName))

  fun forMember(userId: Id<User>, suffix: String = defaultOrder): List<Project> =
    db.select("$selectFrom join project_members pm on $table.id = pm.projectId", ProjectMember::userId eq userId, notDeleted, suffix = suffix) { mapper() }

  fun byCustomer(customerId: Id<Customer>): List<Project> = list(Project::customerId eq customerId)
}
