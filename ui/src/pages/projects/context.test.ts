import {projectTotals} from './context'
import type {LocalDate, MonthlyStats} from 'src/api/types'

const stats: Record<LocalDate, MonthlyStats> = {
  '2026-01-01': {billedHours: 10, unbilledHours: 2, billedRevenue: 1000, unbilledRevenue: 200, workValue: 800},
  '2026-02-01': {billedHours: 5, unbilledHours: 1, billedRevenue: 500, unbilledRevenue: 100, workValue: 400},
}

it('calculates project totals', () => {
  expect(projectTotals(stats)).toEqual({
    totalHours: 18,
    unbilledHours: 3,
    workValue: 1200,
    revenue: 1500,
    unbilledWorkValue: 300,
    profit: 300,
  })
})
