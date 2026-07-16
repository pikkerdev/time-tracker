<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import SortableTable from 'src/components/SortableTable.svelte'
  import api from 'src/api/api'
  import {type Customer, type Id, Status} from 'src/api/types'
  import {t} from 'i18n'
  import Button from 'src/components/Button.svelte'
  import {navigate} from '@keksworks/svelte-tiny-router'
  import Modal from 'src/components/Modal.svelte'
  import CustomerForm from 'src/pages/customers/CustomerForm.svelte'
  import {showToast} from 'src/stores/toasts'
  import {user} from 'src/stores/auth'
  import CheckboxField from 'src/forms/CheckboxField.svelte'

  let customers: Customer[]
  let edit: Customer | false = false
  let deleted: Boolean = false

  async function load(deleted: Boolean){
    customers = await api.get(`customers?isDeleted=${deleted}`)
  }

  async function setStatus(Id: Id<Customer>, status: Status) {
    if (confirm(t.general.deleteConfirm)) {
      await api.post<Customer>(`customers/${Id}`, `"${status}"`)
      if (status == 'ACTIVE') showToast(`${t.general.setActive}`)
      else showToast(`${t.general.deleted}`)
      const index = customers.findIndex(c => c.id === Id)
      customers.splice(index,1)
      customers = customers
    }
  }

  function onSaved(customer: Customer) {
    customers = customers.replaceById(customer)
    edit = false
  }

  $: load(deleted)
</script>

<MainPageLayout class="relative flex flex-col gap-4" title={t.customers.title}>
    <div slot="after-title" class="flex items-center gap-4">
      <CheckboxField label={t.general.showDeleted} onchange={() => deleted = !deleted}/>
      {#if $user.isAdmin}
        <Button label={t.customers.add} onclick={() => edit = {} as Customer}/>
      {/if}
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
          {#if $user.isAdmin}
            <Button label={t.general.edit} onclick={() => edit = structuredClone(c)}/>
            {#if c.status == 'ACTIVE'}
              <Button icon="trash" onclick={() => {setStatus(c.id, Status.DELETED)}}/>
            {:else }
              <Button label={t.general.setActive} onclick={() => {setStatus(c.id, Status.ACTIVE)}}/>
            {/if}
          {/if}
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
