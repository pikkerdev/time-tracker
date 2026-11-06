<script lang="ts">
  import {formatAmount, formatDate, lang, t} from 'i18n'
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import SortableTable from 'src/components/SortableTable.svelte'
  import {type Invoice, type InvoiceId, InvoiceStatus, type InvoiceView, type LocalDate} from 'src/api/types'
  import api from 'src/api/api'
  import Button from 'src/components/Button.svelte'
  import {showToast} from 'src/stores/toasts'
  import Icon from 'src/icons/Icon.svelte'
  import Modal from 'src/components/Modal.svelte'
  import InvoiceForm from 'src/pages/invoices/InvoiceForm.svelte'
  import CheckboxField from 'src/forms/CheckboxField.svelte'
  import BreakdownModal from 'src/pages/invoices/BreakdownModal.svelte'
  import {paidAmount, remainingAmount, totalAmount} from 'src/pages/invoices/invoiceUtils'
  import Form from 'src/forms/Form.svelte'
  import FormField from 'src/forms/FormField.svelte'

  const todayStr = new Date().toISOString().slice(0, 10)

  let invoices: InvoiceView[]
  let invoiceToEdit: Invoice | false = false
  let invoiceToPay: InvoiceView | false = false
  let paymentAmount = 0
  let showPaid: boolean = false
  let breakdown: 'unpaid' | 'overdue' | false = false

  async function load(showPaid: boolean) {
    const params = new URLSearchParams()
    if (showPaid) params.append('showPaid', 'true')
    invoices = await api.get(`invoices?${params}`)
  }

  $: load(showPaid)

  async function del(id: InvoiceId) {
    if (confirm(t.general.deleteConfirm)) {
      const res = await api.delete(`invoices/${id}`)
      if (res) {
        invoices = invoices.filter(i => i.invoice.id !== id)
        showToast(`${t.general.deleted} ${t.invoices.invoice} ${t.general.withId}: ${id}`)
      }
    }
  }

  async function setStatus(invoiceView: InvoiceView, status: InvoiceStatus) {
    if (confirm(`${t.invoices.setInvoiceStatusTo} ${status.toLowerCase()}?`)) {
      const res = await api.post(`invoices/${invoiceView.invoice.id}/status`, `"${status}"`)
      if (res) {
        invoices = invoices.map(i => i.invoice.id === invoiceView.invoice.id ? {...i, invoice: {...i.invoice, status}} : i)
        showToast(`${t.general.statusSetTo}: ${status}`)
      }
    }
  }

  function formatDateMonth(date: LocalDate) {
    return new Date(date).toLocaleDateString(lang, {year: 'numeric', month: 'short'})
  }

  function isOverdue(invoice: InvoiceView): boolean {
    return invoice.invoice.dueDate < todayStr && invoice.invoice.status !== InvoiceStatus.PAID && remainingAmount(invoice) > 0
  }

  $: unpaidTotal = invoices?.filter(i => i.invoice.status !== InvoiceStatus.PAID)
    .reduce((sum, i) => sum + remainingAmount(i), 0) ?? 0

  $: overdueTotal = invoices?.filter(i => isOverdue(i))
    .reduce((sum, i) => sum + remainingAmount(i), 0) ?? 0

  $: breakdownInvoices = (invoices ?? []).filter(i =>
    breakdown === 'overdue' ? isOverdue(i) : i.invoice.status !== InvoiceStatus.PAID && remainingAmount(i) > 0)

  function openPayment(invoice: InvoiceView) {
    invoiceToPay = invoice
    paymentAmount = paidAmount(invoice)
  }

  async function savePayment() {
    if (!invoiceToPay) return
    const saved = await api.post<Invoice>(`invoices/${invoiceToPay.invoice.id}/paidAmount`, JSON.stringify(paymentAmount))
    invoices = invoices.map(i => i.invoice.id === saved.id ? {...i, invoice: saved} : i)
    invoiceToPay = false
    showToast(t.general.saved)
  }

  function onInvoiceSaved(invoice: Invoice) {
    invoices = invoices.map(i => i.invoice.id === invoice.id ? {...i, invoice} : i)
    invoiceToEdit = false
  }
</script>

<MainPageLayout class="relative spaced" title={t.invoices.title}>
  <div slot="after-title" class="flex flex-wrap items-center gap-4">
    <button type="button" class="text-sm hover:underline cursor-pointer" onclick={() => breakdown = 'unpaid'}>
      {t.invoices.unpaid}: <strong>{formatAmount(unpaidTotal)}</strong>
    </button>
    {#if overdueTotal > 0}
      <button type="button" class="text-sm text-red-500 hover:underline cursor-pointer" onclick={() => breakdown = 'overdue'}>
        {t.invoices.overdue}: <strong>{formatAmount(overdueTotal)}</strong>
      </button>
    {/if}
    <CheckboxField label={t.invoices.showPaid} title={t.invoices.showPaid} onchange={() => showPaid = !showPaid}/>
  </div>
  <SortableTable items={invoices} columns={[
    [t.invoices.id, i => i.invoice.id],
    [t.invoices.project, i => `${i.customerName} - ${i.projectName}`],
    [t.invoices.date, i => i.invoice.date],
    [t.invoices.dueDate, i => i.invoice.dueDate],
    [t.invoices.description, i => i.invoice.description],
    [t.invoices.amountWithVat, i => totalAmount(i)],
    [t.invoices.paidAmount, i => paidAmount(i)],
    [t.invoices.remainingAmount, i => remainingAmount(i)],
    [t.general.status, i => i.invoice.status],
    ''
    ]} let:item={i}>
    <tr>
      <td class="whitespace-nowrap">
        <div>{i.invoice.id}</div>
        <div class="text-xs text-gray-400" title={t.invoices.createdBy}>{i.creatorName}</div>
      </td>
      <td>
        <div class="max-w-44 truncate" title={`${i.customerName} - ${i.projectName}`}>{i.customerName} - {i.projectName}</div>
      </td>
      <td class="whitespace-nowrap">
        <div>{formatDate(i.invoice.date)}</div>
        <div class="text-xs text-gray-400" title={t.invoices.revenueMonth}>{formatDateMonth(i.invoice.revenueMonth)}</div>
      </td>
      <td class="whitespace-nowrap" class:text-red-500={isOverdue(i)}>{formatDate(i.invoice.dueDate)}</td>
      <td>
        <div class="max-w-32 truncate" title={i.invoice.description}>{i.invoice.description}</div>
      </td>
      <td class="whitespace-nowrap">{formatAmount(totalAmount(i))}</td>
      <td class="whitespace-nowrap">{formatAmount(paidAmount(i))}</td>
      <td class="whitespace-nowrap">{formatAmount(remainingAmount(i))}</td>
      <td class="whitespace-nowrap">{i.invoice.status}</td>
      <td>
        <div class="flex gap-1 justify-end">
          {#if i.invoice.status === 'CREATED'}
            <Button label={t.invoices.sent} onclick={() => setStatus(i, InvoiceStatus.SENT)}/>
          {:else if i.invoice.status === 'SENT'}
            <Button label={t.invoices.paid} onclick={() => setStatus(i, InvoiceStatus.PAID)}/>
          {/if}
          {#if i.invoice.status !== 'PAID'}
            <Button icon="credit-card" title={t.invoices.payment} onclick={() => openPayment(i)}/>
          {/if}
          <a class="btn default icon-only" href="/invoices/{i.invoice.id}" target="_blank" title={t.invoices.preview}>
            <Icon name="eye"/>
          </a>
          <Button title={t.general.edit} icon="pencil" onclick={() => invoiceToEdit = structuredClone(i.invoice)}/>
          <Button icon="trash" title={t.general.delete} disabled={i.invoice.status === 'PAID'} onclick={() => del(i.invoice.id)}/>
        </div>
      </td>
    </tr>
  </SortableTable>
</MainPageLayout>

<Modal title={t.invoices.edit} bind:show={invoiceToEdit}>
  {#if invoiceToEdit}
    <InvoiceForm invoice={invoiceToEdit} onSaved={onInvoiceSaved}/>
  {/if}
</Modal>

<Modal title={t.invoices.payment} bind:show={invoiceToPay}>
  {#if invoiceToPay}
    <Form submit={savePayment}>
      <div class="flex flex-wrap gap-x-6 gap-y-1 text-sm">
        <span>{t.invoices.amountWithVat}: <strong>{formatAmount(totalAmount(invoiceToPay))}</strong></span>
        <span>{t.invoices.paidAmount}: <strong>{formatAmount(paidAmount(invoiceToPay))}</strong></span>
        <span>{t.invoices.remainingAmount}: <strong>{formatAmount(remainingAmount(invoiceToPay))}</strong></span>
      </div>
      <FormField bind:value={paymentAmount} label={t.invoices.receivedAmount} helpText={t.invoices.paymentHelp}
                 type="number" min="0" max={totalAmount(invoiceToPay)} step="0.01"/>
      <Button class="primary" label={t.invoices.save} type="submit"/>
    </Form>
  {/if}
</Modal>

<BreakdownModal invoices={breakdownInvoices} bind:show={breakdown}/>
