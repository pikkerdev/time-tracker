<script lang="ts">
  import Avatar from 'src/layout/Avatar.svelte'
  import {t} from 'i18n'
  import {user} from 'src/stores/auth'
  import Button from 'src/components/Button.svelte'
  import {Link} from '@keksworks/svelte-tiny-router'
  import {onDestroy} from 'svelte'
  import {slide, type SlideParams, type TransitionConfig} from 'svelte/transition'
  import Logo from 'src/layout/Logo.svelte'

  const largeScreenMedia = window.matchMedia?.('(min-width: 1024px)')

  let menuOpen = false
  let isLargeScreen = largeScreenMedia?.matches ?? window.innerWidth >= 1024

  function updateScreen(event: MediaQueryListEvent) {
    isLargeScreen = event.matches
  }

  largeScreenMedia?.addEventListener('change', updateScreen)
  onDestroy(() => largeScreenMedia?.removeEventListener('change', updateScreen))

  function toggleMenu() {
    menuOpen = !menuOpen
  }

  function closeMenu() {
    menuOpen = false
  }

  function menuSlide(node: Element, params: SlideParams = {}): TransitionConfig {
    return isLargeScreen ? {duration: 0} : slide(node, params)
  }
</script>

<header
  class="bg-stone-50 border-b border-gray-300 px-2 sm:px-3 py-3 flex flex-wrap gap-3 justify-between items-center relative z-10">
  <Link to={$user ? '/entry' : '/'} class="flex gap-2 items-center">
    <Logo/>
  </Link>
  {#if isLargeScreen || menuOpen}
    <div
      class="top-full right-0 left-0 flex flex-col lg:flex-row grow lg:items-center lg:justify-between gap-2 lg:gap-8 z-10 max-lg:bg-stone-50 max-lg:absolute p-2 max-lg:shadow-lg"
      transition:menuSlide>
      <div class="text-lg flex lg:items-center gap-2 lg:gap-6 max-lg:flex-col max-lg:order-2">
        {#if $user}
          <Link to="/projects" label={t.projects.title} onclick={closeMenu}/>
          {#if $user.isAdmin}
            <Link to="/customers" label={t.customers.title} onclick={closeMenu}/>
            <Link to="/users" label={t.users.title} onclick={closeMenu}/>
          {/if}
          {#if $user.isUser || $user.isAdmin}
            <Link to="/timeentries" label={t.timeEntries.title} onclick={closeMenu}/>
          {/if}
          {#if $user.isAdmin}
            <Link to="/invoices" label={t.invoices.title} onclick={closeMenu}/>
          {/if}
        {/if}
      </div>
      <Avatar/>
    </div>
  {/if}
  <Button icon="burger" class="default lg:hidden!" onclick={toggleMenu}/>
</header>

{#if menuOpen && !isLargeScreen}
  <Button class="absolute inset-0 z-9 cursor-default!" onclick={closeMenu}/>
{/if}
