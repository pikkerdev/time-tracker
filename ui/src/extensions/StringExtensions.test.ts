import './StringExtensions'

describe('String extensions', () => {
  it('toTitleCase', () => {
    expect('hello world'.toTitleCase()).to.equal('Hello World')
    expect(''.toTitleCase()).to.equal('')
    expect('hello'.toTitleCase()).to.equal('Hello')
    expect('HELLO WORLD'.toTitleCase()).to.equal('Hello World')
  })
})
