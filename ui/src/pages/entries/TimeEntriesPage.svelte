<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import {
    type Id,
    type Project,
    type TimeEntryView
  } from 'src/api/types'
  import api from 'src/api/api'
  import {t, today} from 'i18n'
  import TimeEntryTable from 'src/pages/entries/TimeEntryTable.svelte'
  import {user} from 'src/stores/auth'
  import FormField from 'src/forms/FormField.svelte'
  import MonthSelectField from 'src/forms/MonthSelectField.svelte'
  import ProjectSelect from 'src/pages/projects/ProjectSelect.svelte'

  let timeEntries: TimeEntryView[]
  let myTimeEntries: boolean = false
  let projectId: Id<Project>
  let from: string
  let to: string

  async function loadEntries(projectId: Id<Project>, from: string, to: string, myTimeEntries: boolean) {
    let url = `projects/timeentries?myTimeEntries=${myTimeEntries}`
    if (projectId) url += `&projectId=${projectId}`
    if (from) url += `&from=${from}`
    if (to) url += `&to=${to}`
    timeEntries = await api.get(url)
  }

  $: loadEntries(projectId, from, to, myTimeEntries)

  $: {
    if (!from || !to) from = from || to
    to = to || from
  }

</script>

<MainPageLayout class="relative spaced" title={t.timeEntries.title}>
  <div slot="title" class="flex items-center gap-4">
    <ProjectSelect bind:projectId/>
    <MonthSelectField bind:from bind:to/>
    <FormField title={t.timeEntries.fromDate} type="date" bind:value={from} max={[to, today].min()}/> -
    <FormField title={t.timeEntries.toDate} type="date" bind:value={to} min={from} max={today}/>
    {#if $user.isAdmin}
      {t.timeEntries.showMyTimeEntries}
      <input title={t.timeEntries.showMyTimeEntries} type="checkbox" bind:checked={myTimeEntries}/>
    {/if}
  </div>
  <TimeEntryTable {timeEntries} onSaved={() => loadEntries(projectId, from, to, myTimeEntries)}/>
</MainPageLayout>
