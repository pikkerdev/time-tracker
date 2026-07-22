<script lang="ts">
  import type { InvoiceRow } from 'src/api/types';
  import FormField from 'src/forms/FormField.svelte'
  import {t} from 'i18n'
  import Button from 'src/components/Button.svelte'

  export let rows: InvoiceRow[] = []
  export let label: string|undefined = undefined
  export let id = label?.replace(/\./g, '-')

  function addRow() {
    rows = [
      ...rows,
      { description: '', hours: undefined, rate: undefined, amount: 0 }
    ];
  }

  function removeRow(index: number) {
    rows = rows.filter((_, i) => i !== index);
  }

  function updateAmount(index: number) {
    const row = rows[index];
    if (row.hours !== undefined && row.rate !== undefined) {
      row.amount = Number((row.hours * row.rate).toFixed(2));
    }
    rows = rows;
  }
</script>

<div class="flex flex-col gap-1">
  {#if label}
    <label for={id} class="text-sm text-gray-700 font-medium mb-1 ">
      {label}
    </label>
  {/if}
  {#if rows.length > 0}
    <div class="flex flex-row items-center gap-2 text-xs font-semibold text-gray-500 mb-1">
      <span class="flex-1">{t.timeEntries.description}</span>
      <span class="flex-1">{t.invoices.hours}</span>
      <span class="flex-1">{t.invoices.rate}</span>
      <span class="flex-1">{t.invoices.amount}</span>
      <div class="w-8 shrink-0"></div>
    </div>
  {/if}
  {#each rows as row, i (i)}
    <div class="flex flex-row items-center gap-2">
      <FormField bind:value={row.description} required={true}/>
      <FormField bind:value={row.hours} oninput={() => updateAmount(i)}/>
      <FormField bind:value={row.rate} oninput={() => updateAmount(i)}/>
      <FormField bind:value={row.amount} required={true} />
      <Button icon="trash" onclick={() => removeRow(i)}/>
    </div>
  {/each}
  <Button label={t.invoices.addRow} onclick={() => addRow()}/>
</div>

