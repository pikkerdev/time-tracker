<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import {
    type TimeEntryView
  } from 'src/api/types'
  import {onMount} from 'svelte'
  import api from 'src/api/api'
  import {t} from 'i18n'
  import TimeEntryTable from 'src/pages/entries/TimeEntryTable.svelte'
  import {user} from 'src/stores/auth'
  import FormField from 'src/forms/FormField.svelte'
  import Button from 'src/components/Button.svelte'

  let timeEntries: TimeEntryView[]
  let myTimeEntries: boolean = false

  let from: string | undefined
  let to: string | undefined

  async function loadEntries(myTimeEntries: boolean) {
    let url = `projects/timeentries?myTimeEntries=${myTimeEntries}`
    if (from) url += `&from=${from}`
    if (to) url += `&to=${to}`
    timeEntries = await api.get(url)
  }

  onMount(async () => {
    await loadEntries(myTimeEntries)
  })

  $: loadEntries(myTimeEntries)
</script>

<MainPageLayout class="relative" title={t.timeEntries.title}>
  <div slot="title" class="flex items-end gap-4">
    <FormField label={t.timeEntries.fromDate} type="date" bind:value={from} />
    <FormField label={t.timeEntries.toDate} type="date" bind:value={to}/>
    <Button label={t.general.filter} onclick={() => loadEntries(myTimeEntries)} />
    {#if $user.isAdmin}
      {t.timeEntries.showMyTimeEntries}
      <input type="checkbox" onchange={() => myTimeEntries = !myTimeEntries}/>
    {/if}
  </div>
  <TimeEntryTable {timeEntries}/>
</MainPageLayout>
