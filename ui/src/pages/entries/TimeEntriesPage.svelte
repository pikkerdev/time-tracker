<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import {type Id, type Project, type TimeEntryView} from 'src/api/types'
  import api from 'src/api/api'
  import {formatAmount, t, today,} from 'i18n'
  import TimeEntryTable from 'src/pages/entries/TimeEntryTable.svelte'
  import {user} from 'src/stores/auth'
  import FormField from 'src/forms/FormField.svelte'
  import MonthSelectField from 'src/forms/MonthSelectField.svelte'
  import ProjectSelect from 'src/pages/projects/ProjectSelect.svelte'
  import Button from 'src/components/Button.svelte'
  import Modal from 'src/components/Modal.svelte'
  import InvoiceForm from 'src/pages/invoices/InvoiceForm.svelte'
  import CheckboxField from 'src/forms/CheckboxField.svelte'

  export let projectId: Id<Project> = ''
  export let selectedEntryIds: string[] = []

  let timeEntries: TimeEntryView[]
  let myTimeEntries = false
  let from: string
  let to: string
  let show = false

  async function loadEntries(from: string, to: string, myTimeEntries: boolean, projectId: Id<Project>) {
    const params = new URLSearchParams({from, to, myTimeEntries: myTimeEntries.toString()})
    if (projectId) params.append('projectId', projectId)
    timeEntries = await api.get(`timeentries?${params}`)
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
  <div slot="after-title" class="flex items-center gap-4">
    <ProjectSelect bind:projectId/>
    <MonthSelectField bind:from bind:to/>
    <FormField title={t.timeEntries.fromDate} type="date" bind:value={from} max={[to, today].min()}/> -
    <FormField title={t.timeEntries.toDate} type="date" bind:value={to} min={from} max={today}/>
    {#if $user.isAdmin}
      <CheckboxField label={t.timeEntries.showMyTimeEntries} title={t.timeEntries.showMyTimeEntries} bind:checked={myTimeEntries} />
    {/if}
  </div>
  <TimeEntryTable bind:selectedEntryIds={selectedEntryIds} {timeEntries} {projectId} onSaved={() => from && to && loadEntries(from, to, myTimeEntries, projectId)}/>
  {#if projectId && selectedEntryIds.length > 0}
    <div class="bg-white shadow-lg border border-b-black fixed bottom-2 left-2 justify-items-center space-y-1 px-2 py-2 rounded-md">
      <div class="font-bold">{t.invoices.totalAmount}: {formatAmount(totalAmount)}</div>
      <Button class="primary" label={t.invoices.create} onclick={() => show = true}/>
    </div>
  {/if}
</MainPageLayout>

<Modal title={t.invoices.create} bind:show>
  <InvoiceForm {selectedEntryIds} bind:show/>
</Modal>
