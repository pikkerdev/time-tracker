package invoices

import ch.tutteli.atrium.api.fluent.en_GB.toEqual
import ch.tutteli.atrium.api.verbs.expect
import db.TestData.invoice
import klite.d
import org.junit.jupiter.api.Test

class InvoiceTest {
  @Test fun `total amount includes vat`() {
    expect(invoice.totalAmount(0.24.d)).toEqual(18.6.d)
  }

  @Test fun `total amount without vat`() {
    expect(invoice.totalAmount(0.d)).toEqual(15.d)
  }
}
