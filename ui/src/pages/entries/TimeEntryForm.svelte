<script lang="ts">
  import Form from 'src/forms/Form.svelte'
  import SelectField from 'src/forms/SelectField.svelte'
  import {t} from 'i18n'
  import type {Project, TimeEntry} from 'src/api/types'
  import {onMount} from 'svelte'
  import FormField from 'src/forms/FormField.svelte'
  import NumberField from 'src/forms/NumberField.svelte'
  import Button from 'src/components/Button.svelte'
  import TextAreaField from 'src/forms/TextAreaField.svelte'
  import api from 'src/api/api'
  import {showToast} from 'src/stores/toasts'

  const LAST_PROJECT_KEY = 'lastProjectId'

  let currentDate = new Date().toISOString().slice(0, 10)
  let projects: Project[] = []

  export let timeEntry:  TimeEntry = {date: currentDate} as TimeEntry

  async function submit() {
    timeEntry = await api.post('projects/timeentry', timeEntry)
    localStorage.setItem(LAST_PROJECT_KEY, timeEntry.projectId)
    showToast(t.general.saved)
    timeEntry = {date: currentDate, projectId: timeEntry.projectId} as TimeEntry
  }

  onMount(
    async function loadProjects()  {
      projects = await api.get('projects?myProjects=true')
      projects.sort((a,b) => a.customerName!.localeCompare(b.customerName!) || a.name.localeCompare(b.name))
    })

</script>

  <Form {submit}>
    <SelectField label={t.projects.project} bind:value={timeEntry.projectId} options={projects.map(p => [p.id, p.customerName? `${p.customerName} ${p.name}` : p.name]).toObject()}/>
    <FormField label={t.timeEntries.date} type="date" bind:value={timeEntry.date} />
    <NumberField label={t.timeEntries.hours} bind:value={timeEntry.hours}/>
    <FormField label={t.timeEntries.storyId} required={false} bind:value={timeEntry.storyId}/>
    <TextAreaField label={t.timeEntries.description} required={false} bind:value={timeEntry.description}/>
    <Button type="submit" label={t.general.save} class="primary"/>
  </Form>
