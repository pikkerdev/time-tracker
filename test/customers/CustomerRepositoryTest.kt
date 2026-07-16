package customers

import ch.tutteli.atrium.api.fluent.en_GB.notToContain
import ch.tutteli.atrium.api.fluent.en_GB.toContain
import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import db.DBTest
import db.Status.ACTIVE
import db.Status.DELETED
import db.TestData.customer
import db.TestData.customer2
import db.TestData.user
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import users.UserRepository

class CustomerRepositoryTest: DBTest() {
  val repository =  CustomerRepository(db)

  @BeforeEach fun setUp(){
    repository.save(customer)
    UserRepository(db).save(user)
  }

  @Test fun `set status`(){
    repository.setStatus(customer.id, DELETED)
    expect(repository.get(customer.id).status).toEqual(DELETED)

    repository.setStatus(customer.id, ACTIVE)
    expect(repository.get(customer.id).status).toEqual(ACTIVE)
  }

  @Test fun list(){
    repository.save(customer2)
    expect(repository.list(false)).toContain(customer)
    expect(repository.list(false)).notToContain(customer2)
    expect(repository.list(true)).toContain(customer2)
    expect(repository.list(true)).notToContain(customer)

  }
}
