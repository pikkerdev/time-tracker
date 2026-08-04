import {act, fireEvent, render} from '@testing-library/svelte'
import PhoneField from './PhoneField.svelte'

describe('PhoneField', () => {
  it('with known country', async () => {
    const {container, component} = render(PhoneField, {value: '3725626262', countryCode: 'EE'})
    const input = container.querySelector('input')!
    expect(input.value).to.eq('+3725626262')
    expect(input.type).to.eq('tel')

    await act(() => component.value = undefined as any)
    expect(input.value).to.eq('')

    await fireEvent.focus(input)
    expect(input.value).to.eq('+372')
    await fireEvent.blur(input)
    expect(input.value).to.eq('')
  })

  it('without known country', async () => {
    const {container} = render(PhoneField, {value: ''})
    const input = container.querySelector('input')!
    expect(input.value).to.eq('')

    await fireEvent.focus(input)
    expect(input.value).to.eq('')
  })
})
