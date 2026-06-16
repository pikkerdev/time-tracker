<script lang="ts">
  import {logout, user} from 'src/stores/auth'
  import {t} from 'i18n'
  import Button from 'src/components/Button.svelte'
  import Dropdown from 'src/components/Dropdown.svelte'

  let isDropDownOpen = false
</script>

{#if $user}
  <Dropdown class="right-0 z-50!" bind:open={isDropDownOpen}>
    <img src={$user.avatarUrl} alt="" class="rounded-xl w-9">
    <svelte:fragment slot="open">
      <div class="min-w-56 p-3">
        <div class="mb-3">
          <span class="font-medium text-gray-900 truncate">{$user.name}</span>
          <span class="text-sm text-gray-500 truncate">{$user.email}</span>
        </div>
        <Button class="w-full justify-start! px-0! hover:bg-gray-50" label={t.login.logout} iconClass="size-5!" icon="logout" onclick={logout}/>
      </div>
    </svelte:fragment>
  </Dropdown>
{:else}
  <a href="/oauth" class="link-button gap-2">
    {t.login.googleLogin}
    <img src="/img/google.svg" class="size-4" title="Google Login" alt="">
  </a>
{/if}
