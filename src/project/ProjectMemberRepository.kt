package project

import db.CrudRepository
import db.Id
import klite.jdbc.*
import project.ProjectMember.Status.DELETED
import users.User
import javax.sql.DataSource

class ProjectMemberRepository(db: DataSource): CrudRepository<ProjectMember>(db, "project_members") {
  private val notDeleted = ProjectMember::status neq DELETED

  fun list(projectId: Id<Project>): List<ProjectMemberUser> =
    db.select("$table join users u on userId = u.id", ProjectMember::projectId to projectId, notDeleted) {
      ProjectMemberUser(create(), create("u."))
    }

  fun isMember(projectId: Id<Project>, userId: Id<User>): Boolean =
    db.count(table, listOf(ProjectMember::projectId to projectId, ProjectMember::userId to userId, notDeleted)) > 0

  fun delete(id: Id<ProjectMember>) {
    db.update(table, mapOf(ProjectMember::status to DELETED), ProjectMember::id to id)
  }

  fun find(projectId: Id<Project>, userId: Id<User>, active: Boolean = true): ProjectMember? =
    db.select(table, ProjectMember::projectId to projectId, ProjectMember::userId to userId, if (active) notDeleted else null)
    { create<ProjectMember>()}.firstOrNull()
}
