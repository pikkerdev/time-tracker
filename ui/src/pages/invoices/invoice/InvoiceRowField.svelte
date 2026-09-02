<script lang="ts">
  import {type InvoiceRow, InvoiceRowType} from 'src/api/types'
  import FormField from 'src/forms/FormField.svelte'
  import {t} from 'i18n'
  import Button from 'src/components/Button.svelte'

  export let rows: InvoiceRow[] = []
  export let label: string|undefined = undefined
  export let id = label?.replace(/\./g, '-')

  function addRow() {
    rows = [
      ...rows,
      { description: '', hours: undefined, rate: undefined, amount: 0, type: InvoiceRowType.CUSTOM }
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
  {#each rows as row, i (i)}
    <div class="flex flex-row items-end gap-2">
      <FormField class="w-full" label={t.timeEntries.description} bind:value={row.description} disabled={row.type === InvoiceRowType.TIMEENTRY}/>
      <FormField label={t.invoices.hours} bind:value={row.hours} oninput={() => updateAmount(i)} required={!!row.rate} disabled={row.type === InvoiceRowType.TIMEENTRY}/>
      <FormField label={t.invoices.rate} bind:value={row.rate} oninput={() => updateAmount(i)} required={!!row.hours} disabled={row.type === InvoiceRowType.TIMEENTRY}/>
      <FormField label={t.invoices.amount} bind:value={row.amount} disabled={row.type === InvoiceRowType.TIMEENTRY || !!row.rate || !!row.hours}/>
      {#if row.type !== InvoiceRowType.TIMEENTRY}
        <Button icon="trash" onclick={() => removeRow(i)}/>
      {/if}
    </div>
  {/each}
  <Button label={t.invoices.addRow} onclick={() => addRow()}/>
</div>
