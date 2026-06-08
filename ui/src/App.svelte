<script lang="ts">
  import {t} from 'i18n'
  import Toasts from 'src/components/Toasts.svelte'
  import {navigate, Route, Router} from 'src/router'
  import HomePage from 'src/pages/home/HomePage.svelte'
  import CustomersPage from 'src/pages/customers/CustomersPage.svelte'
  import ProjectsPage from 'src/pages/projects/ProjectsPage.svelte'
  import ProjectPage from 'src/pages/projects/project/ProjectPage.svelte'
  import UsersPage from 'src/pages/users/UsersPage.svelte'
  import Header from 'src/layout/Header.svelte'
  import TimeEntriesPage from 'src/pages/entries/TimeEntriesPage.svelte'
  import TimeEntryPage from 'src/pages/entries/TimeEntryPage.svelte'
  import {onMount} from 'svelte'
  import {user} from 'src/stores/auth'
  import CreateInvoicePage from 'src/pages/invoices/CreateInvoicePage.svelte'
  import InvoicesPage from 'src/pages/invoices/InvoicesPage.svelte'

  onMount(() => {
    if ($user && location.pathname == '/')
      navigate('/entry')
  })
</script>

<svelte:head>
  <title>{t.title}</title>
</svelte:head>

<Toasts/>

<slot/>

<div class="min-h-screen flex flex-col">
  <Header/>
  <Router>
    <Route path="/" component={HomePage}/>
    <Route path="/entry" component={TimeEntryPage}/>
    <Route path="/customers" component={CustomersPage}/>
    <Route path="/projects" component={ProjectsPage}/>
    <Route path="/projects/:id" component={ProjectPage}/>
    <Route path="/timeentries" component={TimeEntriesPage}/>
    <Route path="/customers/:customerId/projects" component={ProjectsPage}/>
    <Route path="/users" component={UsersPage}/>
    <Route path="/invoices" component={InvoicesPage}/>
    <Route path="/invoices/create" component={CreateInvoicePage}/>
  </Router>
</div>
