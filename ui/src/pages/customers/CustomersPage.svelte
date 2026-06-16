<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import SortableTable from 'src/components/SortableTable.svelte'
  import {onMount} from 'svelte'
  import api from 'src/api/api'
  import type {Customer} from 'src/api/types'
  import {t} from 'i18n'
  import Button from 'src/components/Button.svelte'
  import {navigate} from '@keksworks/svelte-tiny-router'
  import Modal from 'src/components/Modal.svelte'
  import CustomerForm from 'src/pages/customers/CustomerForm.svelte'

  let customers: Customer[]
  let edit: Customer | false = false

  onMount(async () => {
    customers = await api.get('customers')
  })

  function onSaved(customer: Customer) {
    customers = customers.replaceById(customer)
    edit = false
  }
</script>

<MainPageLayout class="relative flex flex-col gap-4" title={t.customers.title}>
  <div slot="after-title">
    <Button label={t.customers.add} onclick={() => edit = {} as Customer}/>
  </div>
  <SortableTable columns={[
    [t.customers.name, 'name'],
    [t.customers.legalName, 'legalName'],
    [t.customers.businessRegistryCode, 'businessRegistryCode'],
    [t.customers.legalAddress, 'legalAddress'],
    [t.customers.vatId, 'vatId'],
    [t.customers.invoiceEmail, 'invoiceEmail'],
    [t.customers.phone, 'phone'],
    ['', '']
  ]} items={customers} let:item={c}>
    <tr>
      <td>{c.name}</td>
      <td>{c.legalName}</td>
      <td>{c.businessRegistryCode}</td>
      <td>{c.legalAddress}</td>
      <td>{c.vatId}</td>
      <td>{c.invoiceEmail}</td>
      <td>{c.phone}</td>
      <td>
        <div class="flex gap-4 justify-end">
          <Button label={t.projects.title} onclick={() => navigate(`/customers/${c.id}/projects`)}/>
          <Button label={t.general.edit} onclick={() => edit = structuredClone(c)}/>
        </div>
      </td>
    </tr>
  </SortableTable>
</MainPageLayout>

<Modal title={t.customers.customer} bind:show={edit}>
  {#if edit}
    <CustomerForm customer={edit} {onSaved}/>
  {/if}
</Modal>
