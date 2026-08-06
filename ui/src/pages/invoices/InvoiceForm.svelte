<script lang="ts">

  import {showToast} from 'src/stores/toasts'
  import api from 'src/api/api'
  import {t, today, toISODate} from 'i18n'
  import type {InvoiceCreateRequest, InvoiceRow, LocalDate} from 'src/api/types'
  import Button from 'src/components/Button.svelte'
  import FormField from 'src/forms/FormField.svelte'
  import Form from 'src/forms/Form.svelte'
  import {navigate} from '@keksworks/svelte-tiny-router'
  import InvoiceRowField from 'src/pages/invoices/invoice/InvoiceRowField.svelte'

  export let selectedEntryIds: string[] = []
  export let show: boolean

  let description: string
  let date = today
  let dueDate: LocalDate = toISODate(today, d => {d.setDate(d.getDate() + 14)
  })
  let rows: InvoiceRow[] = []

  async function submit() {
    const invoiceCreateRequest: InvoiceCreateRequest = {date: date, timeEntryIds: selectedEntryIds, description, dueDate, rows: rows}
    await api.post('invoices', invoiceCreateRequest)
    show = false
    navigate('/invoices')
    showToast(t.general.saved)
  }</script>

<Form {submit}>
  <FormField label={t.invoices.description} bind:value={description}/>
  <FormField label={t.invoices.date} type="date" bind:value={date}/>
  <FormField label={t.invoices.dueDate} type="date" bind:value={dueDate}/>
  <InvoiceRowField label={t.invoices.customRows} bind:rows/>
  <Button class="primary" label={t.invoices.create} type="submit"/>
</Form>

