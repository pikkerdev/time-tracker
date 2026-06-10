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

<div class="mx-auto flex max-w-6xl justify-center px-4 py-6 print:max-w-none print:px-0 print:py-0">
  <Button class="primary" label={t.invoices.create} onclick={createInvoice}/>
</div>

<style>
  @page {
    size: A4;
    margin: 0;
  }

  @media print {
    :global(html),
    :global(body) {
      background: white;
    }

    :global(html) {
      -webkit-print-color-adjust: exact;
      print-color-adjust: exact;
    }
  }
</style>
