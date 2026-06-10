<script lang="ts">

  import {showToast} from 'src/stores/toasts'
  import api from 'src/api/api'
  import {t, today} from 'i18n'
  import type {InvoiceCreateRequest} from 'src/api/types'
  import Button from 'src/components/Button.svelte'

  export let description: string = ''
  export let comment: string = ''
  export let selectedEntryIds: string[] = []

  async function createInvoice() {
    const invoiceCreateRequest: InvoiceCreateRequest = {date: today, timeEntryIds: selectedEntryIds, description, comment}
    await api.post('invoices', invoiceCreateRequest)
    showToast(t.general.saved)
  }
</script>

<Button class="primary" label={t.invoices.create} onclick={createInvoice}/>
