<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import {
    type Id,
    type Project,
    type TimeEntryView
  } from 'src/api/types'
  import api from 'src/api/api'
  import {t} from 'i18n'
  import TimeEntryTable from 'src/pages/entries/TimeEntryTable.svelte'
  import {user} from 'src/stores/auth'
  import FormField from 'src/forms/FormField.svelte'
  import {onMount} from 'svelte'
  import SelectField from 'src/forms/SelectField.svelte'

  let timeEntries: TimeEntryView[]
  let myTimeEntries: boolean = false
  let projects: Project[] = []
  let projectId: Id<Project>

  const formatDate = (date: Date) => new Intl.DateTimeFormat('en-CA').format(date)
  const now = new Date()
  const today = formatDate(now)

  let firstDayOfMonth = formatDate(new Date(now.getFullYear(), now.getMonth(), 1))
  let selectedMonth= ''
  let from = firstDayOfMonth
  let to = today

  $: if (selectedMonth) {
    const [year, month] = selectedMonth.split('-').map(Number)
    from = formatDate(new Date(year, month - 1, 1))
    to = formatDate(new Date(year, month, 0))
  } else {
    from = firstDayOfMonth
    to = today }

  function handleDateChange() {
    selectedMonth = ''
  }

  async function loadEntries(projectId: Id<Project>, from: string, to: string, myTimeEntries: boolean) {
    let url = `projects/timeentries?myTimeEntries=${myTimeEntries}`
    if (projectId) url += `&projectId=${projectId}`
    if (from) url += `&from=${from}`
    if (to) url += `&to=${to}`
    timeEntries = await api.get(url)
  }

  $: loadEntries(projectId, from, to, myTimeEntries)

  onMount(
    async function loadProjects(){
      if ($user.isAdmin) { projects = projects = await api.get('projects?myProjects=false')
      } else { projects = await api.get('projects?myProjects=true') }
    })

</script>

<MainPageLayout class="relative spaced" title={t.timeEntries.title}>
  <div slot="title" class="flex items-center gap-4">
    <SelectField title ={t.projects.chooseProject} bind:value={projectId} emptyOption={t.projects.chooseProject} options={projects.map(p => [p.id,`${p.name}`]).toObject()}/>
    <span>{t.timeEntries.chooseMonth}</span>
    <!-- TODO: fix UI design-->
    <FormField title={t.timeEntries.chooseMonth} type="month" bind:value={selectedMonth} max={today.slice(0, 7)}/>
    <FormField title={t.timeEntries.fromDate} type="date" on:input={handleDateChange} bind:value={from} max={to || today}/> -
    <FormField title={t.timeEntries.toDate} type="date" on:input={handleDateChange} bind:value={to} min={from} max={today}/>
    <!-- TODO:  You should be able to clear the date filter and only filter according to project and vice versa-->
    {#if $user.isAdmin}
      {t.timeEntries.showMyTimeEntries}
      <input title={t.timeEntries.showMyTimeEntries} type="checkbox" bind:checked={myTimeEntries}/>
    {/if}
  </div>
  <TimeEntryTable {timeEntries}/>
</MainPageLayout>
