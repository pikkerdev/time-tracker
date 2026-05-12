<script lang="ts">
  import {type Customer, type Project, Role} from 'src/api/types'
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

  export let project = {} as Project
  export let customers = {} as Customer
  export let label = t.projects.new
  export let show = false
  const isNew = !project.id

  let hourlyRate: number | undefined
  let role = Object.values(Role).filter(r => r !== Role.CUSTOMER)

  $: if (!project.hourlyRates) { project.hourlyRates = {} }

  $: if (isNew) {
   role.forEach(role => {
    project.hourlyRates[role] = hourlyRate})
  }

  async function submit() {
    if (isNew) { project = await api.post('projects', project) }
    else { project = await api.post(`projects/${project.id}`, project) }
    showToast(t.general.saved)
    setTimeout(() => navigate(`/projects/${project.id}`), 500)
    show = false
  }

  async function onclick() {
    show = true
    customers = await api.get('customers')
  }
</script>

<Button {label} {onclick}/>
<Modal bind:show title={label}>
  <Form {submit}>
    <SelectField label={t.customers.customers} bind:value={project.customerId} options={Object.values(customers).map(c => [c.id, c.name]).toObject()} emptyOption=""/>
    <FormField label={t.projects.name} bind:value={project.name}/>
    <TextAreaField label={t.projects.description} bind:value={project.description} rows={3} required={false}/>
    {#if isNew}
      <FormField label={t.projects.hourlyRate} bind:value={hourlyRate}/>
    {:else}
      <p class="text-sm text-black-500 mb-2">{t.projects.hourlyRate}</p>
      <div class="ml-4">
        {#each role as role}
          <FormField label={role.toLowerCase()} bind:value={project.hourlyRates[role]}/>
        {/each}
      </div>
    {/if}
    <FormField label={t.projects.storyTrackerId} bind:value={project.storyTrackerId} required={false}/>
    <div class="flex justify-end items-center pb-2 mt-4">
      <Button type="submit" label={t.general.save} class="primary"/>
    </div>
  </Form>
</Modal>
