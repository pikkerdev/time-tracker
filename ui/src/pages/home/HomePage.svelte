<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import {t} from 'i18n'
  import {user} from 'src/stores/auth'
  import Avatar from 'src/layout/Avatar.svelte'
  import TimeEntryForm from 'src/pages/entries/TimeEntryForm.svelte'
  import type {LocalDate, TimeEntry, TimeEntryView} from 'src/api/types'
  import api from 'src/api/api'
  import TimeEntryTable from 'src/pages/entries/TimeEntryTable.svelte'

  const LAST_PROJECT_KEY = 'lastProjectId'

  let date = new Date().toISOString().slice(0, 10) as LocalDate
  let latestProjectId = localStorage.getItem(LAST_PROJECT_KEY) ?? undefined
  let timeEntries: TimeEntryView[] = []
  let timeEntry: TimeEntry = {date, projectId: latestProjectId} as TimeEntry

  $: loadEntries(date)

  async function loadEntries(date: string) {
    timeEntries = await api.get(`projects/timeentries?myTimeEntries=true&date=${date}`)
  }
</script>

<MainPageLayout class="flex flex-col items-center gap-4 lg:gap-8">
  {#if $user}
    Add your time entry
    <TimeEntryForm bind:timeEntry bind:date/>
    {#if timeEntries.length > 0}
      <TimeEntryTable timeEntries={timeEntries}/>
    {/if}
      <a href = "/timeentries" class="btn default">{t.timeEntries.title} </a>
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
