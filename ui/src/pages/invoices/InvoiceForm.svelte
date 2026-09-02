<script lang="ts">
  import {showToast, ToastType} from 'src/stores/toasts'
  import api from 'src/api/api'
  import {t, today, toISODate} from 'i18n'
  import type {Invoice, InvoiceCreateRequest, InvoiceUpdateRequest} from 'src/api/types'
  import Button from 'src/components/Button.svelte'
  import FormField from 'src/forms/FormField.svelte'
  import Form from 'src/forms/Form.svelte'
  import {navigate} from '@keksworks/svelte-tiny-router'
  import InvoiceRowField from 'src/pages/invoices/invoice/InvoiceRowField.svelte'
  import MonthSelectField from 'src/forms/MonthSelectField.svelte'

  export let selectedEntryIds: string[] = []
  export let show = true
  export let invoice: Invoice | undefined = undefined
  export let onSaved: (invoice: Invoice) => void = () => {}

  let req = invoice ? {...invoice} as InvoiceUpdateRequest : {
    date: today,
    dueDate: toISODate(today, d => { d.setDate(d.getDate() + 14) }),
    revenueMonth: toISODate(today, d => { d.setDate(1) }),
    timeEntryIds: selectedEntryIds
  } as InvoiceCreateRequest

  $: isEdit = !!invoice

  async function submit() {
    if (req.dueDate < req.date) {
      showToast(t.invoices.dueDateBeforeDate, {type: ToastType.ERROR})
      return
    }
    if (invoice) {
      const body = {
        id: invoice.id,
        date: req.date,
        title: req.title.replaceAll(' ', '_'),
        description: req.description,
        dueDate: req.dueDate,
        revenueMonth: req.revenueMonth,
        rows: req.rows
      }
      const saved = await api.post<Invoice>(`invoices/${invoice.id}`, body)
      show = false
      onSaved(saved)
    } else {
      const saved = await api.post<Invoice>('invoices', {...req, title: req.title.replaceAll(' ', '_')})
      show = false
      navigate('/invoices')
    }
    showToast(t.general.saved)
  }
</script>

<Form {submit}>
  <FormField bind:value={req.title} label={t.invoices.invoiceTitle}/>
  <FormField bind:value={req.description} label={t.invoices.description}/>
  <FormField bind:value={req.date} label={t.invoices.date} type="date"/>
  <FormField bind:value={req.dueDate} label={t.invoices.dueDate} type="date" min={req.date}/>
  <MonthSelectField bind:from={req.revenueMonth} to={undefined} label={t.invoices.revenueMonth}/>
  <InvoiceRowField bind:rows={req.rows} label={t.invoices.customRows}/>
  <Button class="primary" label={isEdit ? t.invoices.save : t.invoices.create} type="submit"/>
</Form>
