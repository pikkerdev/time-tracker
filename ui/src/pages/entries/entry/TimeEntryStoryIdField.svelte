<script lang="ts">
  import FormField from 'src/forms/FormField.svelte'
  import Icon from 'src/icons/Icon.svelte'

  export let label: string | undefined
  export let storyIds: number[] = []
  export let id: string|undefined = undefined
  export let required = true

  let value: number | undefined
  let max = 9
  let min = 0

  function remove(i: number) {
    storyIds.splice(i, 1)
    storyIds = storyIds
  }

  function add() {
    if (value) {
      if (!storyIds) storyIds = []
      storyIds.push(value)
      storyIds = storyIds
      value = undefined
    }
  }

  function handleInput(e: Event) {
    const target = e.target as HTMLInputElement
    let currentVal = target.value

    if (currentVal.length >= 9) {
      value = Number(currentVal.slice(0, 9))
      if (!storyIds.includes(value)) add()
    }
  }
</script>

<FormField {label} bind:id {required} >
    <input {id} {required} {min} {max} type="number"  bind:value on:input={handleInput} {...$$restProps}/>
</FormField>
<div class="flex flex-wrap gap-1.5">
  {#each storyIds as s, i}
      <div class="bg-blue-200 rounded-md px-1 flex items-center gap-1">
        <Icon name="storytracker"/> {s} <button type="button" class="ml-1 py-0.5" on:click={() => remove(i)}>✕</button>
      </div>
    {/each}
</div>

