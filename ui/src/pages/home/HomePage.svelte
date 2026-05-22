<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import {t, today, toISODate} from 'i18n'
  import {user} from 'src/stores/auth'
  import Avatar from 'src/layout/Avatar.svelte'
  import TimeEntryForm from 'src/pages/entries/TimeEntryForm.svelte'
  import type {LocalDate, TimeEntry, TimeEntryView} from 'src/api/types'
  import api from 'src/api/api'
  import TimeEntryTable from 'src/pages/entries/TimeEntryTable.svelte'
  import SortableTable from 'src/components/SortableTable.svelte'
  import TimeEntryCalander from 'src/pages/entries/TimeEntryCalander.svelte'

  const LAST_PROJECT_KEY = 'lastProjectId'

  let date = today
  let latestProjectId = localStorage.getItem(LAST_PROJECT_KEY) ?? undefined
  let timeEntries: TimeEntryView[] = []
  let timeEntry: TimeEntry = {date, projectId: latestProjectId} as TimeEntry
  let timeEntryHours: Record<LocalDate, number> = {}

  let dates = Array.from({length: 28}, (_, i) =>
    toISODate(new Date(), d => d.setDate(d.getDate() - i))).toReversed()

  $: loadEntries(date)

  async function loadEntries(date: string) {
    timeEntries = await api.get(`projects/timeentries?myTimeEntries=true&date=${date}`)
    timeEntryHours = await api.get(`projects/timeentries/user?from=${dates[0]}`)
  }
</script>

<MainPageLayout class="flex flex-col gap-4 lg:gap-8">
  {#if $user}
    <div class="flex flex-col gap-4 items-center">
      <TimeEntryCalander bind:date {dates} {timeEntryHours}/>
      <TimeEntryForm bind:timeEntry bind:date/>
      <TimeEntryTable {timeEntries}/>
    </div>
  {:else}
    <div class="flex gap-2 items-center">
      <img src="/favicon.svg" class="size-14 sm:size-28 lg:size-40" title="Time Tracker Logo" alt="Logo">
      <h1 class="font-bold text-4xl sm:text-7xl lg:text-9xl">{t.title}</h1>
    </div>
    <div class="flex flex-col items-center text-center">
      <h4 class="text-lg lg:text-2xl">{t.home.intro}.</h4>
      <h5 class="text-base lg:text-lg">{t.home.slogan}!</h5>
    </div>
    <Avatar/>
  {/if}
</MainPageLayout>
