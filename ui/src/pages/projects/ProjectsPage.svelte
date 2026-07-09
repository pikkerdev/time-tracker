<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import ProjectFormModal from 'src/pages/projects/ProjectFormModal.svelte'
  import type {Customer, Id, Project} from 'src/api/types'
  import api from 'src/api/api'
  import {t} from 'i18n'
  import {Link} from '@keksworks/svelte-tiny-router'
  import {user} from 'src/stores/auth'
  import CheckboxField from 'src/forms/CheckboxField.svelte'

  let projects: Project[] = []
  let isMyProjects: boolean = false
  let showDeleted: boolean = false
  export let customerId: Id<Customer>

  async function load(customerId?: Id<Customer>, myProjects?: Boolean, includeDeleted?: Boolean) {
    const params = new URLSearchParams()
    if (myProjects) params.append('myProjects', myProjects.toString())
    if (includeDeleted) params.append('includeDeleted', includeDeleted.toString())
    let url = `projects?${params}`
    if (customerId) url = `customers/${customerId}/projects`
    projects = await api.get(url)
  }

  $: {load(customerId, isMyProjects, showDeleted)}
</script>

<MainPageLayout class="relative" title={t.projects.title}>
  <div slot="after-title" class="flex items-center gap-4">
    {#if $user.isAdmin || $user.isInternal}
      {#if !customerId}
        <CheckboxField label={t.projects.showMyProjects} title={t.projects.showMyProjects} onchange={() => isMyProjects = !isMyProjects}/>
      {/if}
      {#if $user.isAdmin}
        {#if !customerId}
          <CheckboxField label={t.projects.showDeleted} title={t.projects.showDeleted} onchange={() => showDeleted = !showDeleted}/>
        {/if}
        <ProjectFormModal/>
      {/if}
    {/if}
  </div>
  <div class="grid md:grid-cols-2 lg:grid-cols-4 gap-6 my-3 text-lg">
    {#each projects ?? [] as project}
      <Link to="/projects/{project.id}" class="border border-gray-300 rounded-lg px-4 py-3 bg-white hover:bg-stone-50">
        <h4>{project.customerName} - {project.name}</h4>
        <div class="flex justify-end gap-4 text-sm">
          {#if project.status == 'DELETED'}
            <p><span class="font-medium text-red-700">{project.status}</span></p>
          {/if}
          <div class="w-5 h-5 rounded " style="background-color:{project.color}"></div>
        </div>
      </Link>
    {/each}
  </div>
</MainPageLayout>
