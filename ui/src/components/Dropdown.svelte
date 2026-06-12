<script lang="ts">
  import {fade} from 'svelte/transition'

  export let open = false
  let dropdown: HTMLElement

  function handleOutsideClick(e: Event) {
    if (open && !dropdown.contains(e.target as HTMLElement)) open = false
  }

  function handleEscape(e: KeyboardEvent) {
    if (open && e.key === 'Escape') open = false
  }
</script>

<svelte:body on:click={handleOutsideClick} on:keyup={handleEscape}/>

<div class="shrink-0 relative md:inline-block" bind:this={dropdown}>
  <button onclick={() => open = !open} type="button" tabindex="0"
       class="cursor-pointer">
    <slot/>
  </button>

  {#if open}
    <div out:fade|local={{duration: 200}}
         class="bg-white ring-1 ring-gray-300 focus:outline-none absolute z-10 shadow-lg mt-2 rounded-md overflow-y-auto {$$props.class}"
         role="menu">
      <slot name="open"/>
    </div>
  {/if}
</div>
