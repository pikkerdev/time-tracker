import type {Id, Project, ProjectMemberUser, ProjectView, User} from 'src/api/types'

export type ProjectContext = ProjectView & {
  members: Members
}

export type Members = Record<Id<User>, ProjectMemberUser>

export function isOverBudget(project?: Project): boolean {
  return !!project && project.budget > 0 && project.spent > project.budget
}
