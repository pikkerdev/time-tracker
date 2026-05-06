import type {Id, ProjectMemberUser, ProjectWithCustomer, User} from 'src/api/types'

export type ProjectContext = ProjectWithCustomer & {
  members: Record<Id<User>, ProjectMemberUser>
}
