<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import {type Id, type InvoiceCreateRequest, type Project, type TimeEntryView} from 'src/api/types'
  import api from 'src/api/api'
  import {formatAmount, t, today,} from 'i18n'
  import TimeEntryTable from 'src/pages/entries/TimeEntryTable.svelte'
  import {user} from 'src/stores/auth'
  import FormField from 'src/forms/FormField.svelte'
  import MonthSelectField from 'src/forms/MonthSelectField.svelte'
  import ProjectSelect from 'src/pages/projects/ProjectSelect.svelte'
  import {showToast} from 'src/stores/toasts'
  import Button from 'src/components/Button.svelte'

  export let projectId: Id<Project> = ''

  let timeEntries: TimeEntryView[]
  let myTimeEntries = false
  let from: string
  let to: string
  export let selectedEntryIds: string[] = []
  export let description: string = ''
  export let comment: string = ''

  async function loadEntries(from: string, to: string, myTimeEntries: boolean, projectId: Id<Project>) {
    const params = new URLSearchParams({from, to, myTimeEntries: myTimeEntries.toString()})
    if (projectId) params.append('projectId', projectId)
    timeEntries = await api.get(`projects/timeentries?${params}`)
  }

  async function createInvoice() {
    const invoiceCreateRequest: InvoiceCreateRequest = {date: today, timeEntryIds: selectedEntryIds, description, comment}
    await api.post('invoices', invoiceCreateRequest)
    showToast(t.general.saved)
    await loadEntries(from, to, myTimeEntries, projectId)
  }

  $: if (from && to) loadEntries(from, to, myTimeEntries, projectId)

  $: {
    if (!from || !to) from ||= to
    to ||= from
  }

  $: totalAmount = timeEntries?.filter(e => selectedEntryIds.includes(e.entry.id) && !e.entry.invoiceId)
    .sum(e => e.entry.hourlyRate * e.entry.hours)

  // TODO you can click on invoiceId on an entry that opens an invoice
</script>

<MainPageLayout class="relative spaced" title={t.timeEntries.title}>
  <div slot="title" class="flex items-center gap-4">
    {#if projectId && selectedEntryIds.length > 0}
      <Button class="primary" label={t.invoices.createInvoice} onclick={createInvoice}/>
      <span>{t.invoices.totalAmount}: {formatAmount(totalAmount)}</span>
    {/if}
    <ProjectSelect bind:projectId/>
    <MonthSelectField bind:from bind:to/>
    <FormField title={t.timeEntries.fromDate} type="date" bind:value={from} max={[to, today].min()}/> -
    <FormField title={t.timeEntries.toDate} type="date" bind:value={to} min={from} max={today}/>
    {#if $user.isAdmin}
      {t.timeEntries.showMyTimeEntries}
      <input title={t.timeEntries.showMyTimeEntries} type="checkbox" bind:checked={myTimeEntries}/>
    {/if}
  </div>
  <TimeEntryTable bind:selectedEntryIds={selectedEntryIds} {timeEntries} {projectId} onSaved={() => from && to && loadEntries(from, to, myTimeEntries, projectId)}/>
</MainPageLayout>
