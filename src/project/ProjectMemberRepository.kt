package project

import db.CrudRepository
import db.Id
import klite.jdbc.count
import klite.jdbc.create
import klite.jdbc.neq
import klite.jdbc.select
import klite.jdbc.update
import users.User
import javax.sql.DataSource
import project.Status.DELETED

class ProjectMemberRepository(db: DataSource): CrudRepository<ProjectMember>(db, "project_members") {

  private val notDeleted = ProjectMember::status neq DELETED

  fun list(projectId: Id<Project>): List<ProjectMemberUser> =
    db.select("$table join users u on userId = u.id", ProjectMember::projectId to projectId, notDeleted ) {
      ProjectMemberUser(create(), create("u."))
    }

  fun isMember(projectId: Id<Project>, userId: Id<User>): Boolean =
    db.count(table, listOf(ProjectMember::projectId to projectId, ProjectMember::userId to userId)) > 0

  fun delete(id: Id<ProjectMember>) {
    db.update(table, mapOf(ProjectMember::status to DELETED), ProjectMember::id to id) }
}
