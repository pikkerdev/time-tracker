<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import SortableTable from 'src/components/SortableTable.svelte'
  import {onMount} from 'svelte'
  import api from 'src/api/api'
  import type {Customer} from 'src/api/types'
  import {t} from 'i18n'
  import EditCustomerForm from 'src/pages/customers/EditCustomerForm.svelte'
  import Button from 'src/components/Button.svelte'
  import {navigate} from 'src/router'


  const columns: [string, any][] = [
    [t.customers.name, 'name'],
    [t.customers.legalName, 'legalName'],
    [t.customers.businessRegistryCode, 'businessRegistryCode'],
    [t.customers.legalAddress, 'legalAddress'],
    [t.customers.vatId, 'vatId'],
    [t.customers.invoiceEmail, 'invoiceEmail'],
    [t.customers.phone, 'phone'],
    ['', '']
  ]

  let customers: Customer[]
  let customer = {} as Customer
  let show = false
  let title = ''
  let edit = false

  onMount(
    async () => customers = await api.get('customers')
  )

  function customerAdded(newCustomer: Customer) {
    const existingCustomer = customers.find(c => c.id === newCustomer.id)
    if(existingCustomer) { customers = customers.map(c => c.id === newCustomer.id ? newCustomer : c) }
    else { customers = [newCustomer, ...customers] }
  }

  function resetCustomer() {
    customer = {} as Customer
  }
</script>

<MainPageLayout class="relative flex flex-col gap-4" title={t.customers.title}>
  <div slot="title">
    <Button label={t.customers.add} onclick={() => {
      show = true;
      title = t.customers.add;
      resetCustomer();
      edit = false
    }}/>
  </div>
  <SortableTable {columns} items={customers} let:item>
    <tr>
      <td>{item.name}</td>
      <td>{item.legalName}</td>
      <td>{item.businessRegistryCode}</td>
      <td>{item.legalAddress}</td>
      <td>{item.vatId}</td>
      <td>{item.invoiceEmail}</td>
      <td>{item.phone}</td>
      <td>
        <Button label={t.projects.title} onclick={() => navigate(`/customers/${item.id}/projects`)}/>
        <Button label={t.general.edit} onclick={() => { customer = item; show = true;edit = true; title = t.customers.edit }}/>
      </td>
    </tr>
  </SortableTable>
  <EditCustomerForm bind:show {customer} {title} {edit} onCreated={customerAdded}/>
</MainPageLayout>
