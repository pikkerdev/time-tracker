<script lang="ts">
  import {t} from 'i18n'
  import Toasts from 'src/components/Toasts.svelte'
  import {Route, Router} from 'src/router'
  import HomePage from 'src/pages/HomePage.svelte'
  import CustomersPage from 'src/pages/customers/CustomersPage.svelte'
  import ProjectsPage from 'src/pages/projects/ProjectsPage.svelte'
  import ProjectPage from 'src/pages/projects/ProjectPage.svelte'
  import UsersPage from 'src/pages/users/UsersPage.svelte'
  import Header from 'src/layout/Header.svelte'
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
    <Route path="/customers" component={CustomersPage}/>
    <Route path="/projects" component={ProjectsPage}/>
    <Route path="/projects/:id" component={ProjectPage}/>
    <Route path="/customers/:customerId/projects" component={ProjectsPage}/>
    <Route path="/users" component={UsersPage}/>
  </Router>
</div>
