<script lang="ts">
  import {type Customer, type ProjectDto, Role} from 'src/api/types'
  import Modal from 'src/components/Modal.svelte'
  import {t} from 'i18n'
  import Button from 'src/components/Button.svelte'
  import Form from 'src/forms/Form.svelte'
  import FormField from 'src/forms/FormField.svelte'
  import TextAreaField from 'src/forms/TextAreaField.svelte'
  import api from 'src/api/api'
  import {showToast} from 'src/stores/toasts'
  import SelectField from 'src/forms/SelectField.svelte'
  import {navigate} from 'src/router'
  import NumberField from 'src/forms/NumberField.svelte'

  export let project = {} as ProjectDto
  export let label = t.projects.new

  const isNew = !project.id
  let customers = {} as Customer
  let hourlyRate: number | undefined
  let roles = Object.values(Role).filter(r => r !== Role.CUSTOMER)

  let show = false

  $: if (!project.hourlyRates) { project.hourlyRates = {} }

  $: if (isNew) {
   roles.forEach(role => {
    project.hourlyRates[role] = hourlyRate})
  }

  async function submit() {
    if (isNew) { project = await api.post('projects', project) }
    else { project = await api.post(`projects/${project.id}`, project) }
    showToast(t.general.saved)
    show = false
    if (isNew) {setTimeout(() => navigate(`/projects/${project.id}`), 500)}
    else {window.location.reload()}
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
    {#if isNew}
      <NumberField label={t.projects.hourlyRate} bind:value={hourlyRate} unit="€"/>
    {:else}
      <p class="text-sm text-black-500 mb-2">{t.projects.hourlyRate}</p>
      <div class="ml-4 spaced">
        {#each roles as role}
          <NumberField label={t.members.roles[role]} bind:value={project.hourlyRates[role]} unit="€"/>
        {/each}
      </div>
    {/if}
    <FormField label={t.projects.storyTrackerId} bind:value={project.storyTrackerId} required={false}/>
    <Button type="submit" label={t.general.save} class="primary"/>
  </Form>
</Modal>
