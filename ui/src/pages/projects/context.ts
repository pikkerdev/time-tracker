import type {Id, ProjectDto, ProjectMemberUser, User} from 'src/api/types'

export type ProjectContext = ProjectDto & {
  members: Record<Id<User>, ProjectMemberUser>
}
