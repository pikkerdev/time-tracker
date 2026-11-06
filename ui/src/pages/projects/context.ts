import type {Id, LocalDate, MonthlyStats, Project, ProjectMemberUser, ProjectView, User} from 'src/api/types'

export type ProjectContext = ProjectView & {
  members: Members
}

export type Members = Record<Id<User>, ProjectMemberUser>

export type ProjectTotals = {
  totalHours: number
  unbilledHours: number
  workValue: number
  revenue: number
  unbilledWorkValue: number
  profit: number
}

export function projectTotals(stats: Record<LocalDate, MonthlyStats>): ProjectTotals {
  const values = Object.values(stats)
  const revenue = values.reduce((sum, item) => sum + item.billedRevenue, 0)
  const workValue = values.reduce((sum, item) => sum + item.workValue, 0)
  return {
    totalHours: values.reduce((sum, item) => sum + item.billedHours + item.unbilledHours, 0),
    unbilledHours: values.reduce((sum, item) => sum + item.unbilledHours, 0),
    workValue,
    revenue,
    unbilledWorkValue: values.reduce((sum, item) => sum + item.unbilledRevenue, 0),
    profit: revenue - workValue,
  }
}

export function isOverBudget(project?: Project): boolean {
  return !!project && project.budget > 0 && project.spent > project.budget
}
