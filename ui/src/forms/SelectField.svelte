<script lang="ts">
  import FormField from './FormField.svelte'
  import {onMount} from 'svelte'

  export let label: string|undefined = undefined
  export let id: string|undefined = undefined
  export let emptyOption: string|false = false
  export let placeholder = ''
  export let options: {[value: string|number]: string|number}|string[]|string
  export let value: string|number = ''
  export let required = true
  export let disabled = false
  export let select: HTMLSelectElement|undefined = undefined
  export let onchange: ((e: FormEvent) => void)|undefined = undefined

  $: if (typeof value == 'number') value = value?.toString()
  $: if (value === undefined) value = ''
  $: if (emptyOption && !(options as any)[value]) value = ''

  onMount(() => {
    if (emptyOption === false && !placeholder && !value) value = Object.keys(options)[0]
  })
</script>

<FormField bind:id {label} {required} class={$$props.class}>
  <select {id} bind:this={select} class={$$props.selectClass ?? ''}
          bind:value {required} {disabled} {onchange}>
    {#if emptyOption !== false || placeholder}
      <option value="" disabled={!!placeholder} hidden={!!placeholder && !value}>{emptyOption || placeholder}</option>
    {/if}
    {#each Object.entries(options) as [v, l] (v)}
      {#if l}<option value={v} disabled={!v?.trim()}>{l}</option>{/if}
    {/each}
  </select>
</FormField>

<style>
  select {
    padding-right: 2em !important;
    text-overflow: clip;
  }
</style>
