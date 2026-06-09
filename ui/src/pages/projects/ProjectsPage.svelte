<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import ProjectFormModal from 'src/pages/projects/ProjectFormModal.svelte'
  import type {Customer, Id, Project} from 'src/api/types'
  import api from 'src/api/api'
  import {t} from 'i18n'
  import {Link} from '@keksworks/svelte-tiny-router'
  import {user} from 'src/stores/auth'

  let projects: Project[] = []
  let isMyProjects: boolean = false
  export let customerId: Id<Customer>

  async function load(customerId?: Id<Customer>, myProjects = false) {
    let url = 'projects'
    if (customerId) {
      url = `customers/${customerId}/projects`
    }
    if (myProjects) {
      url += '?myProjects=true'
    }
    projects = await api.get(url)
  }

  $: {load(customerId, isMyProjects)}
</script>

<MainPageLayout class="relative" title={t.projects.title}>
  <div slot="title" class="flex items-center gap-4">
    {#if $user.isAdmin}
      {#if !customerId}
        {t.projects.showMyProjects}
        <input title={t.projects.showMyProjects} type="checkbox" onchange={() => isMyProjects = !isMyProjects}/>
      {/if}
      <ProjectFormModal/>
    {/if}
  </div>
  <div class="grid md:grid-cols-2 lg:grid-cols-4 gap-6 my-3 text-lg">
    {#each projects ?? [] as project}
      <Link to="/projects/{project.id}" class="border border-gray-300 rounded-lg px-4 py-3 bg-white hover:bg-stone-50">
        <h4>{project.name}</h4>
        <div class="flex justify-between text-sm">
          <p><span class="font-medium">{project.customerName}</span></p>
          {#if project.storyTrackerId}
            <p><span class="font-medium">{t.projects.storyTrackerId}</span>: {project.storyTrackerId}</p>
          {/if}
        </div>
      </Link>
    {/each}
  </div>
</MainPageLayout>
