<script lang="ts">
  import {type Customer, type Project, ProjectMemberRole} from 'src/api/types'
  import Modal from 'src/components/Modal.svelte'
  import {formatCurrency, t} from 'i18n'
  import Button from 'src/components/Button.svelte'
  import Form from 'src/forms/Form.svelte'
  import FormField from 'src/forms/FormField.svelte'
  import TextAreaField from 'src/forms/TextAreaField.svelte'
  import api from 'src/api/api'
  import {showToast} from 'src/stores/toasts'
  import SelectField from 'src/forms/SelectField.svelte'
  import {navigate} from '@keksworks/svelte-tiny-router'
  import NumberField from 'src/forms/NumberField.svelte'
  import type {ProjectContext} from 'src/pages/projects/context'

  export let project = {} as ProjectContext
  export let label = t.projects.new

  const isNew = !project.id
  let customers = [] as Customer[]
  let billedRoles = Object.values(ProjectMemberRole).filter(r => r !== ProjectMemberRole.CUSTOMER)

  let show = false

  $: if (!project.hourlyRates) project.hourlyRates = {}

  async function submit() {
    let newProject = project as Project
    const saved = await api.post('projects' + (isNew ? '' : '/' + project.id), newProject) as Project
    project.customerName = customers.find(c => c.id === newProject.customerId)?.name
    showToast(t.general.saved)
    show = false
    if (isNew) setTimeout(() => navigate(`/projects/${saved.id}`), 500)
  }

  async function onclick() {
    show = true
    customers = await api.get('customers')
  }
</script>

<Button {label} {onclick}/>

<Modal bind:show title={label}>
  <Form {submit}>
    <SelectField label={t.customers.customer} bind:value={project.customerId} options={Object.values(customers).map(c => [c.id, c.name]).toObject()} emptyOption=""/>
    <FormField label={t.projects.name} bind:value={project.name}/>
    <TextAreaField label={t.projects.description} bind:value={project.description} rows={3} required={false}/>
    <p class="block text-sm font-medium text-gray-700">{t.projects.hourlyRates}</p>
    <div class="ml-4 spaced">
      {#each billedRoles as role}
        <NumberField label={t.members.roles[role]} bind:value={project.hourlyRates[role]} unit={formatCurrency(project.currency)}/>
      {/each}
    </div>

    <FormField label={t.projects.storyTrackerId} bind:value={project.storyTrackerId} required={false}/>
    <Button type="submit" label={t.general.save} class="primary"/>
  </Form>
</Modal>
