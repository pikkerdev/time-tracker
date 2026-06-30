<script lang="ts">
  import FormField from './FormField.svelte'

  export let label: string | undefined = undefined
  export let value: string | number = '#4a90e2'
  export let unit = ''
  export let min = 0
  export let maxlength = 10

  interface $$Events {input: FormEvent; change: FormEvent}
</script>

<FormField {label} type="number" {min} {maxlength} bind:value style="padding-right: {unit.length / 2 + 1.5}em" on:input on:change {...$$restProps}>
  <input type="color" bind:value={value} style="width: 70px; height: 40px; background-color: {value}"/>

</FormField>

<style>
  input[type="color"]::-webkit-color-swatch {
    border: none;
    background: transparent !important;
  }

  input[type="color"]::-moz-color-swatch {
    border: none;
    background: transparent !important;
  }
</style>
