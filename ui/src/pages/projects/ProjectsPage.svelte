<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import ProjectFormModal from 'src/pages/projects/ProjectFormModal.svelte'
  import type {Customer, Id, ProjectDto} from 'src/api/types'
  import {onMount} from 'svelte'
  import api from 'src/api/api'
  import {t} from 'i18n'
  import {Link} from 'src/router'
  import {user} from 'src/stores/auth'
  import Button from 'src/components/Button.svelte'

  let projects: ProjectDto[] = []
  export let customerId: Id<Customer>

  async function getProjects(customerId: Id<Customer> | undefined = undefined, myProjects: boolean = false) {
    let url = 'projects'
    if (customerId) {
      url = `customers/${customerId}/projects`
    }
    if (myProjects) {
      url += '?myProjects=true'
    }
    projects = await api.get(url)
  }

  onMount(async () => {
    await getProjects(customerId)
  })
</script>

<MainPageLayout class="relative" title={t.projects.title}>
  <div slot="title" class="flex gap-4">
    {#if $user.isAdmin}
      <Button label={t.projects.myProjects} onclick={() => getProjects(customerId, true)}/>
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
