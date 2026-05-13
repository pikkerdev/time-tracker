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

  export let timeEntry:  TimeEntry = {date: new Date().toISOString().slice(0, 10)} as TimeEntry

  let projects: Project[] = []

  async function submit() {
    timeEntry = await api.post('projects/time-entry', timeEntry)
    showToast(t.general.saved)
    timeEntry = {date: new Date().toISOString().slice(0, 10)} as TimeEntry
  }

  onMount(
    async function loadProjects()  {
      projects = await api.get('projects?myProjects=true')
    }
  )

</script>

  <Form {submit}>
    <SelectField label={t.projects.project} bind:value={timeEntry.projectId} options={Object.values(projects).map(p => [p.id, p.name]).toObject()}/>
    <FormField label={t.timeEntries.date} type="date" bind:value={timeEntry.date} />
    <NumberField label={t.timeEntries.hours} bind:value={timeEntry.hours}/>
    <FormField label={t.timeEntries.storyId} required={false} bind:value={timeEntry.storyId}/>
    <TextAreaField label={t.timeEntries.description} required={false} bind:value={timeEntry.description}/>
    <NumberField label={t.projects.hourlyRate} bind:value={timeEntry.hourlyRate}/>
    <Button type="submit" label={t.general.save} class="primary"/>
  </Form>
