<script lang="ts">
  import Avatar from 'src/layout/Avatar.svelte'
  import {t} from 'i18n'
  import {user} from 'src/stores/auth'
  import Button from 'src/components/Button.svelte'
  import {Link} from '@keksworks/svelte-tiny-router'

  let menuOpen = window.innerWidth >= 1024

  function toggleMenu() {
    menuOpen = !menuOpen
  }
</script>

<header
  class="bg-stone-50 border-b border-gray-300 px-2 sm:px-3 py-3 flex flex-wrap gap-3 justify-between items-center relative z-10">
  <Link to={$user ? '/entry' : '/'} class="flex gap-2 items-center">
    <img src="/favicon.svg" class="size-10" title="Time Tracker Logo" alt="Logo">
    <h1 class="font-bold text-2xl">{t.title}</h1>
  </Link>
    <div
      class="top-full right-0 left-0 flex flex-col lg:flex-row grow lg:items-center lg:justify-between gap-2 lg:gap-8 z-10 max-lg:bg-stone-50 max-lg:absolute p-2
      {menuOpen ? 'flex' : 'hidden'} lg:flex">
      <div class="text-lg flex lg:items-center gap-2 lg:gap-6 max-lg:flex-col max-lg:order-2">
        {#if $user}
          <Link to="/projects" label={t.projects.title}/>
          {#if $user.isAdmin}
            <Link to="/customers" label={t.customers.title}/>
            <Link to="/users" label={t.users.title}/>
          {/if}
          {#if $user.isUser || $user.isAdmin}
            <Link to="/timeentries" label={t.timeEntries.title}/>
          {/if}
          {#if $user.isAdmin}
            <Link to="/invoices" label={t.invoices.title}/>
          {/if}
        {/if}
      </div>
      <Avatar/>
    </div>
  <Button icon="burger" class="default lg:hidden!" onclick={toggleMenu}/>
</header>

{#if menuOpen}
  <Button class="absolute inset-0 z-9 cursor-default!" onclick={() => menuOpen = false}/>
{/if}
