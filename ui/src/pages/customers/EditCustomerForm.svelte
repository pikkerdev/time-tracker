<script lang="ts">
  import {slide} from 'svelte/transition'
  import {t} from 'i18n'
  import type {Customer} from 'src/api/types'
  import api from 'src/api/api'
  import {showToast} from 'src/stores/toasts'
  import Button from 'src/components/Button.svelte'
  import Modal from 'src/components/Modal.svelte'
  import Form from 'src/forms/Form.svelte'
  import FormField from 'src/forms/FormField.svelte'

  export let customer = {} as Customer
  export let show = false
  export let title = t.customers.add
  export let edit = false

  let open = false
  export let onCreated: (customer: Customer) => void = () => {}

  async function submit() {
    await api.post('customers', customer)
    showToast(t.general.saved)
    onCreated(customer)
    show = false
  }
</script>

<Modal bind:show title={title}>
  <Form {submit}>
    <div class="sm:flex flex-1 items-end gap-x-2 gap-y-0.5 py-2">
      <FormField class="flex-1" label={t.customers.name} bind:value={customer.name}/>
      {#if !customer.id}
        <Button icon={open ? 'chevron-up' : 'chevron-down'} onclick={() => open = !open} title={t.customers.details}/>
      {/if}
    </div>
    {#if open || edit}
      <div transition:slide class="spaced">
        <FormField required={false} label={t.customers.legalName} bind:value={customer.legalName}/>
        <FormField required={false} label={t.customers.businessRegistryCode} bind:value={customer.businessRegistryCode}/>
        <FormField required={false} label={t.customers.legalAddress} bind:value={customer.legalAddress}/>
        <FormField required={false} label={t.customers.vatId} bind:value={customer.vatId}/>
        <FormField required={false} label={t.customers.email} bind:value={customer.invoiceEmail}/>
        <FormField required={false} label={t.customers.phone} bind:value={customer.phone}/>
      </div>
    {/if}
    <div class="flex justify-between">
      <Button type="submit" label={t.general.save} class="primary"/>
    </div>
  </Form>
</Modal>
