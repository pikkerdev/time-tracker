<script lang="ts">
  export let label: string|undefined = undefined
  export let helpText = ''
  export let checked = false
  export let id = label ? label.replace(/\./g, '-') : ''
  export let group: any[] | undefined = undefined
  export let value: any = undefined
</script>

<div class="relative flex gap-2 items-center {$$props.class ?? ''}">
  <input {id} type="checkbox" {...$$restProps} bind:checked bind:group {value} class="{$$restProps.disabled && 'opacity-50'}">
  <div>
    {#if label}
      <label class="select-none" for={id}>{label}</label>
    {/if}
    {#if helpText}<span class="help-text ml-1 cursor-default" title={helpText}>ⓘ</span>{/if}
    <slot/>
  </div>
</div>

<style>
  @reference 'src/global.css';

  :global(input[type=checkbox]) {
    @apply focus:ring-primary-500 h-4 w-4 text-primary-500 border-gray-300 rounded;
  }
</style>
