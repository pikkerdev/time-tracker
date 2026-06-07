<script lang="ts">
  import {t} from 'i18n'
  import SelectField from 'src/forms/SelectField.svelte'
  import type {Id, Project} from 'src/api/types'
  import {onMount} from 'svelte'
  import {user} from 'src/stores/auth'
  import api from 'src/api/api'

  export let projectId: Id<Project>
  export let isForm: boolean = false
  export let emptyOption: string | undefined = undefined

  let projects: Project[] = []

  onMount(async () => {
    if (!$user.isAdmin || isForm) projects = await api.get('projects?myProjects=true&noCustomer=true')
    else projects = await api.get('projects?myProjects=false')
    projects.sort((a,b) => a.customerName!.localeCompare(b.customerName!) || a.name.localeCompare(b.name))
  })

  $: if (emptyOption === undefined) {
    emptyOption = !isForm ? t.projects.all : undefined
  }
</script>
<SelectField bind:value={projectId} label={isForm? t.projects.project : undefined} {emptyOption}
             options={projects.map(p => [p.id, p.customerName? `${p.customerName} ${p.name}` : p.name]).toObject()}/>
