import {fireEvent, render} from '@testing-library/svelte'
import InvoiceForm from './InvoiceForm.svelte'
import {t} from 'i18n'

it('shows fixed price option when creating an invoice', async () => {
  const {container} = render(InvoiceForm, {selectedEntryIds: ['1', '2']})
  const checkbox = container.querySelector('input[type=checkbox]') as HTMLInputElement

  expect(checkbox).to.not.be.null
  expect(checkbox.checked).to.be.false

  await fireEvent.click(checkbox)

  expect(checkbox.checked).to.be.true
  expect(container.textContent).to.contain(t.invoices.fixedPriceHelp)
})
