<script lang="ts">
  import MainPageLayout from 'src/layout/MainPageLayout.svelte'
  import {t} from 'i18n'
  import {user} from 'src/stores/auth'
  import Avatar from 'src/layout/Avatar.svelte'
  import TimeEntryForm from 'src/pages/home/TimeEntryForm.svelte'
  import {Link} from 'src/router'
</script>

<MainPageLayout class="flex flex-col items-center gap-4 lg:gap-8">
  {#if $user}
    Add your time entry
    <TimeEntryForm/>
    <Link to="/timeentries">{t.timeEntries.title}</Link>
  {:else}
    <div class="flex gap-2 items-center">
      <img src="/favicon.svg" class="size-14 sm:size-28 lg:size-40" title="Time Tracker Logo" alt="Logo">
      <h1 class="font-bold text-4xl sm:text-7xl lg:text-9xl">{t.title}</h1>
    </div>
    <div class="flex flex-col items-center text-center">
      <h4 class="text-lg lg:text-2xl">{t.home.intro}.</h4>
      <h5 class="text-base lg:text-lg">{t.home.slogan}!</h5>
    </div>
    <Avatar/>
  {/if}
</MainPageLayout>
