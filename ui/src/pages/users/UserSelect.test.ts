import {render} from '@testing-library/svelte'
import UserSelect from './UserSelect.svelte'
import api from 'src/api/api'
import {t} from 'i18n'
import {AuthRole, type User} from 'src/api/types'

const users: User[] = [
  {id: '1', email: 'jane@test.ee', firstName: 'Jane', lastName: 'Doe', name: 'Jane Doe', authRole: AuthRole.INTERNAL, createdAt: '2026-01-01T00:00:00Z', isAdmin: false, isCustomer: false, isExternal: false, isInternal: true},
  {id: '2', email: 'john@test.ee', firstName: 'John', lastName: 'Smith', name: 'John Smith', authRole: AuthRole.ADMIN, createdAt: '2026-01-01T00:00:00Z', isAdmin: true, isCustomer: false, isExternal: false, isInternal: false}
]

describe('UserSelect', () => {
  afterEach(() => vi.restoreAllMocks())

  it('lists all users with an all-users option', async () => {
    vi.spyOn(api, 'get').mockResolvedValue(users)
    const {container} = render(UserSelect, {userId: ''})
    await vi.waitFor(() => expect(container.querySelectorAll('option')).to.have.length(3))
    const options = Array.from(container.querySelectorAll('option')).map(o => o.textContent)
    expect(options).to.include(t.timeEntries.allUsers)
    expect(options).to.include('Jane Doe')
    expect(options).to.include('John Smith')
  })
})
