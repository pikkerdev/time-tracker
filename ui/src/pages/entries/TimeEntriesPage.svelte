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

  let timeEntries: TimeEntryView[]
  let myTimeEntries: boolean = false

  const today = new Date()

  const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1)

  let from = firstDayOfMonth.toISOString().split('T')[0]
  let to = today.toISOString().split('T')[0]

  async function loadEntries(myTimeEntries: boolean) {
    let url = `projects/timeentries?myTimeEntries=${myTimeEntries}`
    if (from) url += `&from=${from}`
    if (to) url += `&to=${to}`
    timeEntries = await api.get(url)
  }

  onMount(async () => {
    await loadEntries(myTimeEntries)
  })

  $: {
    from
    to
    myTimeEntries
    loadEntries(myTimeEntries)
  }
</script>

<MainPageLayout class="relative spaced" title={t.timeEntries.title}>
  <div slot="title" class="flex items-center gap-4">
    <FormField title={t.timeEntries.fromDate} type="date" bind:value={from}/> -
    <FormField title={t.timeEntries.toDate} type="date" bind:value={to}/>
    {#if $user.isAdmin}
      {t.timeEntries.showMyTimeEntries}
      <input title={t.timeEntries.showMyTimeEntries} type="checkbox" onchange={() => myTimeEntries = !myTimeEntries}/>
    {/if}
  </div>
  <TimeEntryTable {timeEntries}/>
</MainPageLayout>
