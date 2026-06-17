<script lang="ts">
  import Avatar from 'src/layout/Avatar.svelte'
  import {t} from 'i18n'
  import {user} from 'src/stores/auth'
  import Button from 'src/components/Button.svelte'
  import {activePath, Link} from '@keksworks/svelte-tiny-router'
  import {onDestroy} from 'svelte'
  import {slide, type SlideParams, type TransitionConfig} from 'svelte/transition'
  import Logo from 'src/layout/Logo.svelte'
  import Icon from 'src/icons/Icon.svelte'

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

{#snippet bottomLine(selected: boolean)}
  <div
    class="h-0.5 bg-pikker-gold w-0 transition-all duration-300 rounded-xl {selected ? 'w-full' : 'group-hover:w-10/12'}">
  </div>
{/snippet}

{#snippet mainLink(to: string, label: string, icon: string)}
  {@const selected = $activePath.startsWith(to)}
  <Link {to} onclick={closeMenu} class="flex flex-col text-xl group">
    <div class="flex items-center gap-1">
      <Icon name={icon}/>
      {label}
    </div>
    {@render bottomLine(selected)}
  </Link>
{/snippet}

<header
  class="bg-stone-50 border-b border-gray-300 px-2 sm:px-3 py-3 flex flex-wrap gap-3 justify-between items-center relative z-10">
  <Link class="group" to={$user ? '/entry' : '/'}>
    <Logo selected={$activePath.startsWith($user ? '/entry' : '/')}/>
  </Link>
  {#if isLargeScreen || menuOpen}
    <div
      class="top-full right-0 left-0 flex flex-col lg:flex-row grow lg:items-center lg:justify-between gap-2 lg:gap-8 z-10 max-lg:bg-stone-50 max-lg:absolute p-2 max-lg:shadow-lg"
      transition:menuSlide>
      <div class="text-lg flex lg:items-center gap-2 lg:gap-6 max-lg:flex-col max-lg:order-2">
        {#if $user}
          {#if $user.isUser || $user.isAdmin}
            {@render mainLink("/timeentries", t.timeEntries.title, "clock")}
          {/if}
          {@render mainLink("/projects", t.projects.title, "code-folder")}
          {#if $user.isAdmin}
            {@render mainLink("/customers", t.customers.title, "building")}
            {@render mainLink("/invoices", t.invoices.title, "document")}
            {@render mainLink("/users", t.users.title, "users")}
          {/if}
        {/if}
      </div>
    </div>
  {/if}
  <div class="flex gap-2">
    <Button class="default lg:hidden!" icon="burger" onclick={toggleMenu}/>
    <Avatar/>
  </div>
</header>

{#if menuOpen && !isLargeScreen}
  <Button class="absolute inset-0 z-9 cursor-default!" onclick={closeMenu}/>
{/if}
