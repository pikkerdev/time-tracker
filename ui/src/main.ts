import {mount} from 'svelte'
import './extensions/ArrayExtensions'
import './extensions/StringExtensions'
import './global.css'
import App from './App.svelte'
import {initErrorHandlers} from 'src/api/errorHandlers'
import {initSession} from 'src/stores/auth'
import type {User} from 'src/api/types'
import api from 'src/api/api'

(async function() {
  initErrorHandlers()

  const [auth] = await Promise.all([api.get('user').catch(() => null)])
  if (auth) initSession(auth as User)

  document.body.innerHTML = ''
  mount(App, {target: document.body})
}())
