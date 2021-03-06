(ns cloth.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            cloth.core-test
            cloth.util-test
            cloth.keys-test
            cloth.tx-test
            cloth.net-test
            cloth.contracts-test
            cloth.filters-test
            ))

(doo-tests
          'cloth.contracts-test
          'cloth.core-test
           'cloth.util-test
           'cloth.keys-test
           'cloth.tx-test
           'cloth.net-test
           'cloth.filters-test
           )

