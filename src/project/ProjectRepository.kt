package project

import customers.Customer
import db.CrudRepository
import db.Id
import db.json
import klite.i18n.Lang.jsonMapper
import klite.jdbc.create
import klite.jdbc.get
import klite.jdbc.select
import klite.json.parse
import klite.toValues
import users.User
import java.math.BigDecimal
import java.sql.ResultSet
import javax.sql.DataSource

class ProjectRepository(db: DataSource): CrudRepository<Project>(db, "projects") {
  override fun Project.persister() = toValues(Project::hourlyRates to json(hourlyRates))
  override fun ResultSet.mapper() = create(Project::hourlyRates to jsonMapper.parse<Map<Role, BigDecimal>>(getString("hourlyRates")))

  private fun ResultSet.projectDtoMapper(): ProjectDto {
    val project = mapper()
    return ProjectDto(
      id = project.id,
      customerName = get<String>("c.name"),
      name = project.name,
      description = project.description,
      currency = project.currency,
      hourlyRates = project.hourlyRates,
      storyTrackerId = project.storyTrackerId
    )
  }

  fun getDto(id: Id<Project>): ProjectDto? =
    db.select("$table p join customers c on p.customerId = c.id", "p.id" to id.value) {
      projectDtoMapper()
    }.firstOrNull()

  fun dtoList(): List<ProjectDto> =
    db.select("$table p join customers c on p.customerId = c.id") {
      projectDtoMapper()
    }

  fun dtoListForMember(userId: Id<User>): List<ProjectDto> =
    db.select(
      "$table p join customers c on p.customerId = c.id join project_members pm on p.id = pm.projectId",
      listOf("pm.userId" to userId.value)
    ) {
      projectDtoMapper()
    }

  fun dtoListByCustomer(customerId: Id<Customer>): List<ProjectDto> =
    db.select(
      "$table p join customers c on p.customerId = c.id", "p.customerId" to customerId.value
    ) {
      projectDtoMapper()
    }
}
