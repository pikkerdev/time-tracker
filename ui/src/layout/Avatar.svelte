<script lang="ts">
  import {logout, user} from 'src/stores/auth'
  import {t} from 'i18n'
  import Button from 'src/components/Button.svelte'
  import Dropdown from 'src/components/Dropdown.svelte'

  let isDropDownOpen = false
</script>

{#if $user}
  <Dropdown class="right-0 z-50!" bind:open={isDropDownOpen}>
    <Button class="gap-2">
      <img src={$user.avatarUrl} alt="" class="rounded-full w-8">
      {$user.firstName + " " + $user.lastName}
    </Button>
    <svelte:fragment slot="open">
      <div class="py-0.5 px-1">
        <Button label={t.login.logout} size="sm" icon="logout" onclick={logout}/>
      </div>
    </svelte:fragment>
  </Dropdown>
{:else}
  <a href="/oauth" class="link-button gap-2">
    {t.login.googleLogin}
    <img src="/img/google.svg" class="size-4" title="Google Login" alt="">
  </a>
{/if}
