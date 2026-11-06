import {InvoiceRowType, InvoiceStatus, type InvoiceView} from 'src/api/types'
import {amount, paidAmount, remainingAmount, totalAmount} from './invoiceUtils'

function invoiceView(options: {paidAmount?: number, status?: InvoiceStatus} = {}): InvoiceView {
  return {
    creatorName: 'Tester',
    customerName: 'Acme',
    projectName: 'Alpha',
    vat: 0.24,
    invoice: {
      date: '2026-01-01',
      description: '',
      dueDate: '2026-01-01',
      fixedPrice: false,
      id: 1,
      paidAmount: options.paidAmount ?? 0,
      projectId: '1',
      revenueMonth: '2026-01-01',
      rows: [{amount: 100, description: '', type: InvoiceRowType.CUSTOM}],
      status: options.status ?? InvoiceStatus.SENT,
      title: ''
    }
  }
}

describe('invoiceUtils', () => {
  it('calculates total amount with vat', () => {
    expect(amount(invoiceView())).to.eq(100)
    expect(totalAmount(invoiceView())).to.eq(124)
  })

  it('calculates remaining amount from partial payment', () => {
    const invoice = invoiceView({paidAmount: 50})
    expect(paidAmount(invoice)).to.eq(50)
    expect(remainingAmount(invoice)).to.eq(74)
  })

  it('treats paid invoices as fully paid', () => {
    const invoice = invoiceView({status: InvoiceStatus.PAID})
    expect(paidAmount(invoice)).to.eq(124)
    expect(remainingAmount(invoice)).to.eq(0)
  })

  it('does not return negative remaining amount', () => {
    expect(remainingAmount(invoiceView({paidAmount: 200}))).to.eq(0)
  })
})
