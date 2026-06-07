<script lang="ts">

  import {t, today} from 'i18n'
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
  let projectId: Id<Project> = ''
  let from : string | undefined
  let to : string | undefined
  let selectedEntryIds: string[] = []

  async function createInvoice() {
    const invoiceCreateRequest: InvoiceCreateRequest = {date: today, timeEntryIds: selectedEntryIds}
    await api.post('invoices', invoiceCreateRequest)
    showToast(t.general.saved)
  }

  async function loadEntries(projectId: Id<Project>, from: string, to: string) {
    const params = new URLSearchParams({from, to})
    params.append("projectId", projectId)
    timeEntries = await api.get(`projects/timeentries?${params}`)
  }

  $: if (projectId && from && to) loadEntries(projectId, from, to)

  $: sum = timeEntries?.filter(entry => selectedEntryIds.includes(entry.entry.id) && !entry.entry.invoiceId)
    .reduce((sum, entry) => sum +(entry.entry.hourlyRate), 0)
</script>

<!-- todo preview for an invoice -->

<MainPageLayout class="relative spaced" title={t.invoices.title}>
  <div class="justify-items-start flex items-center gap-4">
    {#if projectId}
      <Button class="primary" label={t.invoices.createInvoice} onclick={createInvoice}/>
      <span>{t.invoices.sum}: {sum} €</span>
    {/if}
  </div>
  <div slot="title" class="flex items-center gap-4">
    {#if !projectId}
      <h5>{t.invoices.chooseProject}</h5>
    {/if}
    <ProjectSelect emptyOption={t.projects.chooseProject} bind:projectId/>
    <FormField title={t.timeEntries.fromDate} type="date" bind:value={from} max={[to, today].min()}/> -
    <FormField title={t.timeEntries.toDate} type="date" bind:value={to} min={from} max={today}/>
    <MonthSelectField bind:from bind:to/>
  </div>
  {#if projectId}
    <TimeEntryTable {timeEntries} isInvoicePage={true} bind:selectedEntryIds={selectedEntryIds}/>
  {/if}
</MainPageLayout>
