<script lang="ts">
  import FormField from 'src/forms/FormField.svelte'
  export let label: string|undefined = undefined
  export let activity: string|undefined
  export let projectActivities: string[]
  export let required = true
  export let id: string|undefined = undefined

  let focused = false

</script>
<FormField bind:id {label} {required} {...$$restProps} >
  <input {id} bind:value={activity} onfocus={() => focused = true} onblur={() => focused = false}
         {required} list="activities">
  {#if focused}
    <datalist id="activities">
      {#each projectActivities as t}
        <option>{t}</option>
      {/each}
    </datalist>
  {/if}
</FormField>
