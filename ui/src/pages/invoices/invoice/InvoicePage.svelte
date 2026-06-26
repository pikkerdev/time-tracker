<script lang="ts">
  import {formatAmount, formatDate, t} from 'i18n'
  import Button from 'src/components/Button.svelte'
  import type {InvoiceDetails, InvoiceId} from 'src/api/types'
  import {onMount} from 'svelte'
  import api from 'src/api/api'

  export let id: InvoiceId

  let details: InvoiceDetails
  onMount(async () => {
    details = await api.get(`invoices/${id}`)
  })

  function print() {
    window.print()
  }
</script>

<div class="fixed top-2 left-2 flex gap-2 no-print">
  <Button class="rounded-full! size-12! bg-white" icon="arrowhead-left" iconClass="size-6!"
          onclick={() => history.back()}/>
  <Button class="rounded-full! size-12! bg-primary text-white" icon="printer" iconClass="size-6!" onclick={print}/>
</div>

<div class="invoice-bg flex justify-center p-8 bg-gray-100 min-h-screen">
  <div
    class="invoice-page bg-white box-border w-[210mm] min-h-[297mm] p-[20mm_25mm] shadow-lg print:w-full print:min-h-0 print:p-[15mm_20mm] print:shadow-none">
    <header class="flex justify-between items-start mb-8 pb-6 border-b-2 border-gray-800">
      <img alt="Logo" class="h-12 print:h-8" src="/own-logo.svg"/>
      <div class="text-right">
        <h1
          class="text-3xl font-bold text-gray-800 mb-2 tracking-wide print:text-2xl print:mb-1">{t.invoices.invoice.toUpperCase()}</h1>
        <div class="text-sm text-gray-500">
          <span class="font-semibold text-gray-600">{t.invoices.invoiceNo}:</span> {details?.invoice.id}
        </div>
        <div class="text-sm text-gray-500">
          <span class="font-semibold text-gray-600">{t.invoices.date}:</span> {formatDate(details?.invoice.date)}
        </div>
        <div class="text-sm text-gray-500">
          <span class="font-semibold text-gray-600">{t.invoices.dueDate}:</span> {formatDate(details?.invoice.dueDate)}
        </div>
      </div>
    </header>

    <div class="grid grid-cols-2 gap-8 mb-8 print:gap-6 print:mb-4">
      <div>
        <h4 class="section-label">{t.invoices.from}</h4>
        <p class="party-name">{t.company.name}</p>
        <p class="section-text">{t.company.address}</p>
        <p class="section-text">{t.company.phone}</p>
        <p class="section-text">{t.company.email}</p>
        <p class="section-text">{t.invoices.vat}: {t.company.vatId}</p>
      </div>
      <div>
        <h4 class="section-label">{t.invoices.billTo}</h4>
        <p class="party-name">{details?.customer.legalName}</p>
        <p class="section-text">{details?.customer.legalAddress}</p>
        <p class="section-text">{details?.customer.invoiceEmail}</p>
        {#if details?.customer.vatId}
          <p class="section-text">{t.invoices.vat}: {details?.customer.vatId}</p>
        {/if}
      </div>
    </div>

    <div class="mb-8 print:mb-4">
      <p class="section-text font-bold">{details?.invoice.description}</p>
    </div>

    <table class="w-full border-collapse mb-8 print:mb-4">
      <thead>
      <tr>
        <th class="th text-left!">{t.invoices.itemDescription}</th>
        <th class="th">{t.invoices.hours}</th>
        <th class="th">{t.invoices.rate}</th>
        <th class="th">{t.invoices.amount}</th>
      </tr>
      </thead>
      <tbody>
      {#each Object.entries(details?.entries ?? {}) as [role, [hours, rate]]}
        <tr>
          <td class="td">{role.toTitleCase()} {t.invoices.workingHour}</td>
          <td class="td td-num">{hours}</td>
          <td class="td td-num">{formatAmount(rate)}</td>
          <td class="td td-num">{formatAmount(hours * rate)}</td>
        </tr>
      {/each}
      </tbody>
      <tfoot>
      <tr>
        <td class="tfoot-label" colspan="3">{t.invoices.subtotal}</td>
        <td class="tfoot-value">{formatAmount(details?.invoice.amount ?? 0)}</td>
      </tr>
      <tr>
        <td class="tfoot-label" colspan="3">{t.invoices.vat} ({details?.vat * 100}%)</td>
        <td class="tfoot-value">{formatAmount(details?.invoice.vatAmount ?? 0)}</td>
      </tr>
      <tr>
        <td class="tfoot-label tfoot-label-total" colspan="3">{t.invoices.totalAmount}</td>
        <td
          class="tfoot-value tfoot-value-total">{formatAmount((details?.invoice.amount ?? 0) + (details?.invoice.vatAmount ?? 0))}</td>
      </tr>
      </tfoot>
    </table>

    <footer class="grid grid-cols-2 gap-8 mt-12 pt-6 border-t border-gray-200 print:mt-4 print:pt-3 print:gap-6">
      <div>
        <h4 class="section-label">{t.invoices.paymentDetails}</h4>
        <p class="section-text"><span class="text-gray-500">{t.invoices.bankAccount}:</span> {t.company.bankAccount}</p>
        <p class="section-text"><span class="text-gray-500">{t.invoices.bank}:</span> {t.company.bank}</p>
        <p class="section-text"><span class="text-gray-500">{t.invoices.iban}:</span> {t.company.iban}</p>
        <p class="section-text"><span class="text-gray-500">{t.invoices.reference}: {details?.invoice.id}</span></p>
      </div>
      <div>
        <h4 class="section-label">{t.invoices.contact}</h4>
        <p class="section-text">{t.company.contactPerson.name}</p>
        <p class="section-text">{t.company.contactPerson.phone}</p>
        <p class="section-text">{t.company.contactPerson.email}</p>
      </div>
    </footer>
  </div>
</div>

<style>
  @reference 'src/global.css';

  @page {
    size: A4;
    margin: 0;
  }

  .section-label {
    @apply text-xs font-semibold uppercase tracking-wider text-gray-500 mb-2;
  }

  .section-text {
    @apply text-sm text-gray-700 mb-1;
  }

  .party-name {
    @apply font-semibold text-gray-900 text-sm mb-1;
  }

  .th {
    @apply text-right py-3 px-4 text-xs font-semibold text-gray-600 uppercase tracking-wider border-b-2 border-gray-200 bg-gray-50 print:py-2 print:px-3 print:text-[0.7rem];
  }

  .td {
    @apply py-3 px-4 text-sm border-b border-gray-100 print:py-2 print:px-3 print:text-xs;
  }

  .td-num {
    @apply text-right tabular-nums;
  }

  .tfoot-label {
    @apply font-semibold text-right pr-4 pt-3 border-t-2 border-gray-200 print:pt-2;
  }

  .tfoot-label-total {
    @apply font-bold text-base text-gray-900 border-t-2 border-gray-800;
  }

  .tfoot-value {
    @apply text-right py-3 px-4 tabular-nums border-t-2 border-gray-200 print:py-2 print:px-3;
  }

  .tfoot-value-total {
    @apply font-bold text-base text-gray-900 border-t-2 border-gray-800;
  }

  @media print {
    :global(html),
    :global(body) {
      @apply bg-white m-0 p-0;
    }

    :global(html) {
      -webkit-print-color-adjust: exact;
      print-color-adjust: exact;
    }

    .no-print {
      display: none !important;
    }

    .invoice-bg {
      @apply bg-white p-0;
    }

    .section-label {
      @apply mb-1;
    }

    .section-text {
      @apply text-xs mb-0.5;
    }
  }
</style>
