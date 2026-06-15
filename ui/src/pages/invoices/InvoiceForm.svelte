<script lang="ts">

  import {showToast} from 'src/stores/toasts'
  import api from 'src/api/api'
  import {t, today, toISODate} from 'i18n'
  import type {InvoiceCreateRequest, LocalDate} from 'src/api/types'
  import Button from 'src/components/Button.svelte'
  import FormField from 'src/forms/FormField.svelte'
  import Form from 'src/forms/Form.svelte'

  export let selectedEntryIds: string[] = []
  export let show: boolean

  let description: string = ''
  let date = today
  let dueDate: LocalDate = toISODate(today, d => {d.setDate(d.getDate() + 14)
  })

  async function submit() {
    const invoiceCreateRequest: InvoiceCreateRequest = {date: today, timeEntryIds: selectedEntryIds, description, dueDate}
    await api.post('invoices', invoiceCreateRequest)
    showToast(t.general.saved)
    show = false

  }</script>

<Form {submit}>
  <FormField label={t.invoices.description} bind:value={description}></FormField>
  <FormField label={t.invoices.date} type="date" bind:value={date}></FormField>
  <FormField label={t.invoices.dueDate} type="date" bind:value={dueDate}></FormField>
  <Button class="primary" label={t.invoices.create} onclick={submit}/>
</Form>

