<script lang="ts">

import InvoiceContent from 'src/pages/invoices/InvoiceContent.svelte'

export let selectedEntryIds: string[] = []

</script>

<div class="mx-auto flex max-w-6xl justify-center px-4 py-6 print:max-w-none print:px-0 print:py-0">
  <InvoiceContent {selectedEntryIds}/>
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
