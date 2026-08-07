<script lang="ts">
  import {showToast} from 'src/stores/toasts'
  import api from 'src/api/api'
  import {t, today, toISODate} from 'i18n'
  import type {InvoiceCreateRequest} from 'src/api/types'
  import Button from 'src/components/Button.svelte'
  import FormField from 'src/forms/FormField.svelte'
  import Form from 'src/forms/Form.svelte'
  import {navigate} from '@keksworks/svelte-tiny-router'
  import InvoiceRowField from 'src/pages/invoices/invoice/InvoiceRowField.svelte'

  export let selectedEntryIds: string[] = []
  export let show: boolean

  let req = {
    date: today,
    dueDate: toISODate(today, d => { d.setDate(d.getDate() + 14) }),
    timeEntryIds: selectedEntryIds
  } as InvoiceCreateRequest

  async function submit() {
    await api.post('invoices', {...req, title: req.title.replaceAll(' ', '_')})
    show = false
    navigate('/invoices')
    showToast(t.general.saved)
  }
</script>

<Form {submit}>
  <FormField bind:value={req.title} label={t.invoices.invoiceTitle}/>
  <FormField bind:value={req.description} label={t.invoices.description}/>
  <FormField bind:value={req.date} label={t.invoices.date} type="date"/>
  <FormField bind:value={req.dueDate} label={t.invoices.dueDate} type="date"/>
  <InvoiceRowField bind:rows={req.rows} label={t.invoices.customRows}/>
  <Button class="primary" label={t.invoices.create} type="submit"/>
</Form>

