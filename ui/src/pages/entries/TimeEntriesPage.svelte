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

  let timeEntries: TimeEntryView[]
  let myTimeEntries: boolean = false

  async function loadEntries(myTimeEntries = false){
    timeEntries = await api.get(`projects/timeentries?myTimeEntries=${myTimeEntries}`)
  }

  onMount(async () => {
    await loadEntries(myTimeEntries)
  })

  $: loadEntries(myTimeEntries)
</script>

<MainPageLayout class="relative" title={t.timeEntries.title}>
  <div slot="title" class="flex items-center gap-4">
    {#if $user.isAdmin}
      {t.timeEntries.showMyTimeEntries}
      <input type="checkbox" onchange={() => myTimeEntries = !myTimeEntries}/>
    {/if}
  </div>
  <TimeEntryTable {timeEntries}/>
</MainPageLayout>
