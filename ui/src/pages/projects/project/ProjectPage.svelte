<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import {type Id, type Project, ProjectMemberRole, type ProjectMemberUser, Status} from 'src/api/types'
  import {onMount} from 'svelte'
  import api from 'src/api/api'
  import {formatAmount, t} from 'i18n'
  import ProjectFormModal from 'src/pages/projects/ProjectFormModal.svelte'

  import {isOverBudget, type ProjectContext} from 'src/pages/projects/context'
  import ProjectMembersModal from 'src/pages/projects/project/ProjectMembersModal.svelte'
  import {user} from 'src/stores/auth'
  import Button from 'src/components/Button.svelte'
  import {showToast} from 'src/stores/toasts'
  import {navigate} from '@keksworks/svelte-tiny-router'
  import Icon from 'src/icons/Icon.svelte'
  import ProjectBarChart from 'src/pages/projects/project/ProjectBarChart.svelte'

  export let id: Id<Project>

  let ctx: ProjectContext | undefined

  $: stats = Object.values(ctx?.stats ?? {})
  $: totalHours = stats.reduce((sum, item) => sum + item.unbilledHours + item.billedHours, 0)
  $: unbilledHours = stats.reduce((sum, item) => sum + item.unbilledHours, 0)
  $: totalRevenue = stats.reduce((sum, item) => sum + item.unbilledRevenue + item.billedRevenue, 0)
  $: unbilledRevenue = stats.reduce((sum, item) => sum + item.unbilledRevenue, 0)
  $: budgetRemaining = (ctx?.project?.budget ?? 0) - totalRevenue

  onMount(async () => {
    ctx = await api.get('projects/' + id)
    api.get<ProjectMemberUser[]>(`projects/${id}/members`).then(r => {
      ctx!.members = r.indexBy(m => m.user.id)
    })
  })

  async function deleteProject(Id: Id<Project>) {
    if (confirm(t.general.deleteConfirm)) {
      await api.delete(`projects/${Id}`)
      navigate(`/projects`)
      showToast(`${t.general.deleted} ${ctx?.project?.name}`)
    }
  }

</script>

<MainPageLayout class="relative">
  <div slot="title">
    <div class="flex items-center gap-2">
      <h1>{ctx?.project?.customerName} - {ctx?.project?.name}</h1>
      {#if ctx?.project?.status ===  Status.DELETED}
        <span class="text-muted">({t.general.deleted.toUpperCase()})</span>
      {/if}
    </div>
    {#if ctx?.project?.storyTrackerId}
      <a class="flex items-center gap-1 group" href="https://story.pikker.dev/projects/{ctx.project.storyTrackerId}"
         target="_blank">
        <Icon name="storytracker" class="group-hover:rotate-180 transition-all duration-300"/>
        <span
          class="text-sm text-muted group-hover:text-blue-600 trainsiton-colors duration-300">{ctx.project.storyTrackerId}</span>
      </a>
    {/if}
  </div>
  <div class="flex flex-wrap gap-4" slot="after-title">
    {#if ctx && $user.isAdmin}
      <ProjectMembersModal members={ctx.members} projectId={ctx.project.id}/>
      <ProjectFormModal bind:project={ctx.project} label={t.projects.edit}/>
      <Button type="button" icon="trash" title={t.members.deleteMember} onclick={() => deleteProject(id)}/>
    {/if}
  </div>
  <div class="grid grid-cols-1">
    <p class="text-lg">{ctx?.project?.description}</p>
    <div class="flex flex-wrap gap-2 mt-4">
      <div class="grid grid-cols-1 gap-x-16 gap-y-4">
        <div class="flex flex-col">
          <span class="text-lg font-semibold">{t.projects.overview}</span>
          <hr class="text-pikker-gold border-t-3">
          <div class="grid grid-cols-2 gap-x-4">
            <span>{t.projects.totalHours}</span>
            <span>{totalHours}</span>
            <span>{t.projects.unbilledHours}</span>
            <span>{unbilledHours}</span>
            <span>{t.projects.totalRevenue}</span>
            <span>{formatAmount(totalRevenue, ctx?.project?.currency)}</span>
            <span>{t.projects.unbilledRevenue}</span>
            <span>{formatAmount(unbilledRevenue, ctx?.project?.currency)}</span>
            <span>{t.projects.budget}</span>
            <span>{formatAmount(ctx?.project?.budget ?? 0, ctx?.project?.currency)}</span>
            <span>{t.projects.budgetRemaining}</span>
            <span class:text-red-600={isOverBudget(ctx?.project)}>{formatAmount(budgetRemaining, ctx?.project?.currency)}</span>
              </div>
        </div>
        <div class="flex flex-col">
          <span class="text-lg font-semibold">{t.projects.hourlyRates}</span>
          <hr class="text-pikker-gold border-t-3">
          <div class="grid grid-cols-2 gap-x-4">
            {#each Object.values(ProjectMemberRole) as role}
              {@const rate = ctx?.project?.hourlyRates[role]}
              {#if rate}
                <span class="justify-start">{t.members.roles[role]}</span>
                <span>{formatAmount(rate, ctx?.project?.currency)}</span>
              {/if}
            {/each}
          </div>
        </div>
        {#if ctx?.members}
          <div class="flex flex-col">
            <span class="text-lg font-semibold">{t.members.title}</span>
            <hr class="text-pikker-gold border-t-3">
            <div class="flex flex-col">
              {#each Object.values(ctx.members) as member}
                <span>{member.user.name} - {member.member.role.toTitleCase()}</span>
              {/each}
            </div>
          </div>
        {/if}
        <div class="flex flex-col max-w-80">
          <span class="text-lg font-semibold">{t.timeEntries.activities}</span>
          <hr class="text-pikker-gold border-t-3">
          <p>{ctx?.project?.activities?.map(a => a.split(',')).join(", ")}</p>
        </div>
      </div>
      <div class="grow" >
        {#if ctx}
          <ProjectBarChart label={t.projects.monthlyHoursAndRevenue} data={ctx.stats}/>
          {/if}
      </div>
    </div>
  </div>
</MainPageLayout>
