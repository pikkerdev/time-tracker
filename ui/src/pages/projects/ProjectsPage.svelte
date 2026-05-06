<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import NewProjectButton from 'src/pages/projects/NewProjectButton.svelte'
  import type {Customer, Id, Project, ProjectWithCustomer} from 'src/api/types'
  import {onMount} from 'svelte'
  import api from 'src/api/api'
  import {t} from 'i18n'
  import {Link} from 'src/router'
  import {user} from 'src/stores/auth'
  import Button from 'src/components/Button.svelte'

  let projects: ProjectWithCustomer[] = []
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

<MainPageLayout class="relative">
  <div class="flex justify-end">
    <div class="flex gap-4">
      <Button label={t.projects.myProjects} onclick={() => getProjects(customerId, true)}/>
      {#if $user.authRole === 'ADMIN'}
        <NewProjectButton/>
      {/if}
    </div>
  </div>
  <div class="grid md:grid-cols-2 lg:grid-cols-4 gap-6 my-3 text-lg">
    {#each projects ?? [] as p}
      <Link to="/projects/{p.project.id}" class="border border-gray-300 rounded-lg px-4 py-3 bg-white hover:bg-stone-50">
        <h4>{p.project.name}</h4>
        <div class="flex justify-between text-sm">
          <p><span class="font-medium">{p.customer.name}</span></p>
          {#if p.project.storyTrackerId}
            <p><span class="font-medium">{t.projects.storyTrackerId}</span>: {p.project.storyTrackerId}</p>
          {/if}
        </div>
      </Link>
    {/each}
  </div>
</MainPageLayout>
