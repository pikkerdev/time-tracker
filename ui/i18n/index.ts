import type en from './en.json'
import langs from './langs.json'
import type {CountryCode, LocalDate} from 'src/api/types'
import countriesData from './countries.json'

export const countries: Record<CountryCode, {vatRate: number, currency: string, phoneAreaCode: string}> = countriesData

export function changeLang(lang: typeof langs[number]) {
  localStorage['lang'] = lang
  location.reload()
}

export function changeLangInUrl(lang: typeof langs[number]) {
  const url = new URL(location.href)
  url.searchParams.set('lang', lang)
  location.href = url.toString()
}

export function resolve(key: string, from: Record<string, any> = t): any {
  return key.split('.').reduce((acc, key) => acc && acc[key], from)
}

function choosePreferredLang() {
  const urlLang = new URLSearchParams(location.search).get('lang')
  if (urlLang && langs.includes(urlLang)) return urlLang
  let lang = localStorage?.['lang'] ?? navigator.language.split('-')[0]
  return langs.includes(lang) ? lang : langs[0]
}

async function load() {
  if (lang === 'en') return (await import('./en.json')).default
  if (lang === 'et') return (await import('./et.json')).default
  else throw new Error('Unsupported lang: ' + lang)
}

export {langs}
export const langsOptions = Object.fromEntries(langs.map(l => [l, l.toUpperCase()]))
export const lang = choosePreferredLang()
export let t: typeof en = await load()

export function formatCurrency(code: string) {
  return !code || code === 'EUR' ? '€' : code
}

export function formatAmount(amount: number, currency = 'EUR') {
  const [integer, decimals] = amount.toFixed(2).split('.')
  const grouped = integer.replace(/\B(?=(\d{3})+(?!\d))/g, '\u00A0')
  return `${grouped}.${decimals}\u00A0${formatCurrency(currency)}`
}

export function formatDate(date: Date | string) {
  return new Date(date).toLocaleDateString()
}

export function toISODate(date: Date | string, transform?: (d: Date) => void) {
  const d = new Date(date)
  transform?.(d)
  return d.toLocaleDateString('lt') as LocalDate
}

export const today = toISODate(new Date())

export type Values = {
  [name: string]: any;
};

export function replaceValues(text: string, values: Values) {
  return text.replace(/\{(.*?)}/g, (_, placeholder) => replacePlaceholder(placeholder, values));
}

function replacePlaceholder(text: string, values: Values) {
  const pluralTokens = text.split('|');
  const field = pluralTokens[0];
  if (pluralTokens.length == 1)
    return values[field] ?? field;
  const key = new Intl.PluralRules(lang).select(values[field]);
  const zeroKey = values[field] === 0 ? 'zero' : '';
  for (let i = 1; i < pluralTokens.length; i++) {
    const [candidateKey, candidateText] = pluralTokens[i].split(':', 2);
    if (candidateKey === key || candidateKey == zeroKey)
      return candidateText.replace('#', values[field]);
  }
  return field;
}
