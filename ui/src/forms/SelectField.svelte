<script lang="ts">
  import FormField from './FormField.svelte'
  import {onMount} from 'svelte'

  export let label: string|undefined = undefined
  export let id: string|undefined = undefined
  export let emptyOption: string|false = false
  export let emptyOptionAsClear = false
  export let emptyOptionOnOpen: string|undefined = undefined
  export let options: {[value: string|number]: string|number}|string[]|string
  export let value: string|number = ''
  export let required = true
  export let disabled = false
  export let select: HTMLSelectElement|undefined = undefined
  export let onchange: ((e: FormEvent) => void)|undefined = undefined

  let emptyOptionText: string|false = emptyOption
  let isFocused: boolean = false

  $: emptyOptionText = !value && emptyOption !== false ? emptyOption : isFocused && emptyOptionOnOpen ? emptyOptionOnOpen : emptyOption
  $: if (typeof value == 'number') value = value?.toString()

  function handleFocus() { isFocused = true }

  function handleBlur() { isFocused = false }

  onMount(() => {
    if (emptyOption === false && !value) value = Object.keys(options)[0]
  })

  $: if (emptyOption && !(options as any)[value]) value = ''
</script>

<FormField bind:id {label} {required} class={$$props.class}>
  <select {id} bind:this={select} class={$$props.selectClass ?? ''}
          bind:value {required} {disabled} {onchange} onfocus={handleFocus} onblur={handleBlur}>
    {#if emptyOption !== false}
      <option value="" disabled={!emptyOptionAsClear} hidden={!emptyOptionAsClear && !value}>{emptyOptionText}</option>
    {/if}
    {#each Object.entries(options) as [v, l] (v)}
      {#if l}<option value={v} disabled={!v?.trim()}>{l}</option>{/if}
    {/each}
  </select>
</FormField>

<style>
  select {
    padding-right: 2em !important;
  }
</style>
