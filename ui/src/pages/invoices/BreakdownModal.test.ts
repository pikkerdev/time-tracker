import {fireEvent, render} from '@testing-library/svelte'
import BreakdownModal from './BreakdownModal.svelte'
import {formatAmount} from 'i18n'
import {InvoiceRowType, InvoiceStatus, type InvoiceView} from 'src/api/types'

function invoiceView(customerName: string, projectName: string, amounts: number[]): InvoiceView {
  return {
    creatorName: 'Tester',
    customerName,
    projectName,
    invoice: {
      date: '2026-01-01',
      description: '',
      dueDate: '2026-01-01',
      id: 1,
      projectId: '1',
      revenueMonth: '2026-01-01',
      rows: amounts.map(amount => ({amount, description: '', type: InvoiceRowType.CUSTOM})),
      status: InvoiceStatus.SENT,
      title: ''
    }
  }
}

const invoices = [
  invoiceView('Acme', 'Alpha', [100]),
  invoiceView('Acme', 'Beta', [200])
]

const checkbox = (index: number) =>
  document.body.querySelectorAll('input[type=checkbox]')[index] as HTMLInputElement

const selectedTotal = () => document.body.querySelector('strong')?.textContent

it('selects projects individually and through the customer row', async () => {
  render(BreakdownModal, {invoices, show: 'unpaid'})
  expect(document.body.querySelectorAll('input[type=checkbox]').length).to.eq(3)
  expect(selectedTotal()).to.be.undefined

  await fireEvent.click(checkbox(1))
  expect(selectedTotal()).to.eq(formatAmount(100))

  await fireEvent.click(checkbox(0))
  expect(checkbox(1).checked).to.be.true
  expect(checkbox(2).checked).to.be.true
  expect(selectedTotal()).to.eq(formatAmount(300))

  await fireEvent.click(checkbox(1))
  expect(checkbox(0).checked).to.be.false
  expect(checkbox(0).indeterminate).to.be.true
  expect(selectedTotal()).to.eq(formatAmount(200))
})
