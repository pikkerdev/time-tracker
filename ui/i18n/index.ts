import type en from './en.json'
import langs from './langs.json'

export function changeLang(lang: typeof langs[number]) {
  localStorage['lang'] = lang
  location.reload()
}

export function resolve(key: string, from: Record<string, any> = t): any {
  return key.split('.').reduce((acc, key) => acc && acc[key], from)
}

function choosePreferredLang() {
  let lang = localStorage['lang'] ?? navigator.language.split('-')[0]
  return langs.includes(lang) ? lang : langs[0]
}

async function load() {
  if (lang === 'en') return (await import('./en.json')).default
  else throw new Error('Unsupported lang: ' + lang)
}

export const lang = choosePreferredLang()
export let t: typeof en = await load()

export function formatCurrency(code: string) {
  return !code || code === 'EUR' ? '€' : code
}

export function formatAmount(amount: number, currency: string) {
  return `${amount.toFixed(2)}\u00A0${currency}`
}
