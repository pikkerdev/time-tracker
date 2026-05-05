<script lang="ts">
  import Avatar from 'src/layout/Avatar.svelte'

  import {t} from 'i18n'
  import Navigation from 'src/layout/Navigation.svelte'
  import {Link} from 'src/router'
</script>

<header class="bg-stone-50 border-b border-gray-300 px-2 sm:px-3 py-3 flex flex-wrap gap-3 justify-between items-center">
  <div class="flex items-center gap-8">
    <Link to="/" class="flex gap-2 items-center">
      <img src="/favicon.svg" class="size-10" title="Time Tracker Logo" alt="Logo">
      <h1 class="font-bold text-2xl">{t.title}</h1>
    </Link>
    <Navigation/>
  </div>
  <Avatar/>
</header>
