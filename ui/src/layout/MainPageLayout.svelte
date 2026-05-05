<script lang="ts">
  import {t} from 'i18n'
  import Header from 'src/layout/Header.svelte'

  export let title = ''
</script>

<svelte:head>
  <title>{t.title}{title ? " - " + title : ""}</title>
</svelte:head>

<div class="min-h-screen flex flex-col">
  <Header>
    <slot name="header"/>
  </Header>
  <main class="px-2 pt-6 sm:px-3 grow bg-stone-100 {$$restProps.class}">
    <slot/>
  </main>
</div>
