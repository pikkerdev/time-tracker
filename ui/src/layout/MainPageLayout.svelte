<script lang="ts">
  import {t} from 'i18n'

  export let title = ''
</script>

<svelte:head>
  <title>{t.title}{title ? " - " + title : ""}</title>
</svelte:head>

<main class="px-2 pt-6 sm:px-3 grow bg-stone-100 {$$restProps.class}">
  <slot/>
</main>
