<script lang="ts">
  import Avatar from 'src/layout/Avatar.svelte'

  import {t} from 'i18n'
  import Navigation from 'src/layout/Navigation.svelte'
</script>

<header class="bg-stone-50 border-b px-2 sm:px-3 py-3 flex flex-wrap gap-3 justify-between items-center">
  <div class="flex items-center gap-8">
    <a href="/" class="flex gap-2 items-center">
      <img src="/favicon.svg" class="size-10" title="Time Tracker Logo" alt="Logo">
      <h1 class="font-bold text-2xl">{t.title}</h1>
    </a>
    <Navigation/>
  </div>
  <Avatar/>
</header>
