<script lang="ts">
  import {formatAmount, t, today} from 'i18n'
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import MonthSelectField from 'src/forms/MonthSelectField.svelte'
  import ProjectSelect from 'src/pages/projects/ProjectSelect.svelte'
  import FormField from 'src/forms/FormField.svelte'
  import type {Id, InvoiceCreateRequest, Project, TimeEntryView} from 'src/api/types'
  import api from 'src/api/api'
  import {showToast} from 'src/stores/toasts'
  import TimeEntryTable from 'src/pages/entries/TimeEntryTable.svelte'
  import Button from 'src/components/Button.svelte'

  let timeEntries: TimeEntryView[]
  const LAST_PROJECT_KEY_INVOICE = 'lastProjectIdInvoice' // TODO: reuse lastProjectId, move to another file
  let projectId: Id<Project> = localStorage.getItem(LAST_PROJECT_KEY_INVOICE) || ''
  let from : string
  let to : string
  let selectedEntryIds: string[] = []

  async function createInvoice() {
    const invoiceCreateRequest: InvoiceCreateRequest = {date: today, timeEntryIds: selectedEntryIds}
    await api.post('invoices', invoiceCreateRequest)
    localStorage.setItem(LAST_PROJECT_KEY_INVOICE, projectId)
    showToast(t.general.saved)
    await loadEntries(projectId, from, to)
  }

  async function loadEntries(projectId: Id<Project>, from: string, to: string) {
    const params = new URLSearchParams({from, to})
    params.append("projectId", projectId)
    timeEntries = await api.get(`projects/timeentries?${params}`)
  }

  $: if (projectId && from && to) loadEntries(projectId, from, to)

  $: totalAmount = timeEntries?.filter(e => selectedEntryIds.includes(e.entry.id) && !e.entry.invoiceId)
    .sum(e => e.entry.hourlyRate * e.entry.hours)
</script>

<!-- todo preview for an invoice -->

<MainPageLayout class="relative spaced" title={t.invoices.createInvoice}>
  <div class="justify-items-start flex items-center gap-4">
    {#if projectId && selectedEntryIds.length > 0}
      <Button class="primary" label={t.invoices.createInvoice} onclick={createInvoice}/>
      <span>{t.invoices.totalAmount}: {formatAmount(totalAmount)}</span>
    {/if}
  </div>
  <div slot="title" class="flex items-end gap-4">
    <ProjectSelect bind:projectId showLastProject={true}/>
    <FormField title={t.timeEntries.fromDate} type="date" bind:value={from} max={[to, today].min()}/> -
    <FormField title={t.timeEntries.toDate} type="date" bind:value={to} min={from} max={today}/>
    <MonthSelectField bind:from bind:to/>
  </div>
  {#if projectId}
    <TimeEntryTable {timeEntries} bind:selectedEntryIds={selectedEntryIds}/>
  {/if}
</MainPageLayout>
