<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import {t} from 'i18n'
  import {user} from 'src/stores/auth'
  import Avatar from 'src/layout/Avatar.svelte'
</script>

<MainPageLayout class="flex flex-col items-center gap-8">
  {#if $user}
    Insert Dashboard here :P
  {:else}
    <div class="flex gap-2 items-center">
      <img src="/favicon.svg" class="size-40" title="Time Tracker Logo" alt="Logo">
      <h1 class="font-bold text-9xl">{t.title}</h1>
    </div>
    <div class="flex flex-col items-center">
      <h4>{t.home.intro}.</h4>
      <h5>{t.home.slogan}!</h5>
    </div>
    <Avatar/>
  {/if}
</MainPageLayout>
