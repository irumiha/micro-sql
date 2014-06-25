package microsql

import SQL._

/**
 * An attempt at simplifying the extracting of ResultSets to case classes.
 *
 * WIP
 */
object Extractors {

  def mapping[R, T1](rs: RichResultSet, apply: (T1) => R)(implicit c1: RichResultSet => T1): R = {
    apply(rs)
  }


  def mapping[R, T1, T2](rs: RichResultSet, apply: (T1, T2) => R)(implicit c1: RichResultSet => T1,
                                                                  c2: RichResultSet => T2): R = {
    apply(rs, rs)
  }

  def mapping[R, T1, T2, T3](rs: RichResultSet, apply: (T1, T2, T3) => R)(implicit c1: RichResultSet => T1,
                                                                          c2: RichResultSet => T2,
                                                                          c3: RichResultSet => T3): R = {
    apply(rs, rs, rs)
  }

  def mapping[R, T1, T2, T3, T4](rs: RichResultSet, apply: (T1, T2, T3, T4) => R)(implicit c1: RichResultSet => T1,
                                                                                  c2: RichResultSet => T2,
                                                                                  c3: RichResultSet => T3,
                                                                                  c4: RichResultSet => T4): R = {
    apply(rs, rs, rs, rs)
  }

  def mapping[R, T1, T2, T3, T4, T5](rs: RichResultSet, apply: (T1, T2, T3, T4, T5) => R)(implicit c1: RichResultSet => T1,
                                                                                          c2: RichResultSet => T2,
                                                                                          c3: RichResultSet => T3,
                                                                                          c4: RichResultSet => T4,
                                                                                          c5: RichResultSet => T5): R = {
    apply(rs, rs, rs, rs, rs)
  }

  def mapping[R, T1, T2, T3, T4, T5, T6]
  (rs: RichResultSet, apply: (T1, T2, T3, T4, T5, T6) => R)(implicit c1: RichResultSet => T1,
                                         c2: RichResultSet => T2,
                                         c3: RichResultSet => T3,
                                         c4: RichResultSet => T4,
                                         c5: RichResultSet => T5,
                                         c6: RichResultSet => T6): R = {
    apply(rs, rs, rs, rs, rs, rs)
  }

  def mapping[R, T1, T2, T3, T4, T5, T6, T7]
  (rs: RichResultSet, apply: (T1, T2, T3, T4, T5, T6, T7) => R)(implicit c1: RichResultSet => T1,
                                             c2: RichResultSet => T2,
                                             c3: RichResultSet => T3,
                                             c4: RichResultSet => T4,
                                             c5: RichResultSet => T5,
                                             c6: RichResultSet => T6,
                                             c7: RichResultSet => T7): R = {
    apply(rs, rs, rs, rs, rs, rs, rs)
  }

  def mapping[R, T1, T2, T3, T4, T5, T6, T7, T8]
  (rs: RichResultSet, apply: (T1, T2, T3, T4, T5, T6, T7, T8) => R)(implicit c1: RichResultSet => T1,
                                                 c2: RichResultSet => T2,
                                                 c3: RichResultSet => T3,
                                                 c4: RichResultSet => T4,
                                                 c5: RichResultSet => T5,
                                                 c6: RichResultSet => T6,
                                                 c7: RichResultSet => T7,
                                                 c8: RichResultSet => T8): R = {
    apply(rs, rs, rs, rs, rs, rs, rs, rs)
  }

  def mapping[R, T1, T2, T3, T4, T5, T6, T7, T8, T9]
  (rs: RichResultSet, apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9) => R)(implicit c1: RichResultSet => T1,
                                                     c2: RichResultSet => T2,
                                                     c3: RichResultSet => T3,
                                                     c4: RichResultSet => T4,
                                                     c5: RichResultSet => T5,
                                                     c6: RichResultSet => T6,
                                                     c7: RichResultSet => T7,
                                                     c8: RichResultSet => T8,
                                                     c9: RichResultSet => T9): R = {
    apply(rs, rs, rs, rs, rs, rs, rs, rs, rs)
  }

  def mapping[R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10]
  (rs: RichResultSet, apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) => R)(implicit c1: RichResultSet => T1,
                                                          c2: RichResultSet => T2,
                                                          c3: RichResultSet => T3,
                                                          c4: RichResultSet => T4,
                                                          c5: RichResultSet => T5,
                                                          c6: RichResultSet => T6,
                                                          c7: RichResultSet => T7,
                                                          c8: RichResultSet => T8,
                                                          c9: RichResultSet => T9,
                                                          c10: RichResultSet => T10): R = {
    apply(rs, rs, rs, rs, rs, rs, rs, rs, rs, rs)
  }

  def mapping[R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11]
  (rs: RichResultSet, apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) => R)(implicit c1: RichResultSet => T1,
                                                               c2: RichResultSet => T2,
                                                               c3: RichResultSet => T3,
                                                               c4: RichResultSet => T4,
                                                               c5: RichResultSet => T5,
                                                               c6: RichResultSet => T6,
                                                               c7: RichResultSet => T7,
                                                               c8: RichResultSet => T8,
                                                               c9: RichResultSet => T9,
                                                               c10: RichResultSet => T10,
                                                               c11: RichResultSet => T11): R = {
    apply(rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs)
  }

  def mapping[R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12]
  (rs: RichResultSet, apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) => R)(implicit c1: RichResultSet => T1,
                                                                    c2: RichResultSet => T2,
                                                                    c3: RichResultSet => T3,
                                                                    c4: RichResultSet => T4,
                                                                    c5: RichResultSet => T5,
                                                                    c6: RichResultSet => T6,
                                                                    c7: RichResultSet => T7,
                                                                    c8: RichResultSet => T8,
                                                                    c9: RichResultSet => T9,
                                                                    c10: RichResultSet => T10,
                                                                    c11: RichResultSet => T11,
                                                                    c12: RichResultSet => T12): R = {
    apply(rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs)
  }

  def mapping[R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13]
  (rs: RichResultSet, apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) => R)(implicit c1: RichResultSet => T1,
                                                                         c2: RichResultSet => T2,
                                                                         c3: RichResultSet => T3,
                                                                         c4: RichResultSet => T4,
                                                                         c5: RichResultSet => T5,
                                                                         c6: RichResultSet => T6,
                                                                         c7: RichResultSet => T7,
                                                                         c8: RichResultSet => T8,
                                                                         c9: RichResultSet => T9,
                                                                         c10: RichResultSet => T10,
                                                                         c11: RichResultSet => T11,
                                                                         c12: RichResultSet => T12,
                                                                         c13: RichResultSet => T13): R = {
    apply(rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs)
  }

  def mapping[R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14]
  (rs: RichResultSet, apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) => R)(implicit c1: RichResultSet => T1,
                                                                              c2: RichResultSet => T2,
                                                                              c3: RichResultSet => T3,
                                                                              c4: RichResultSet => T4,
                                                                              c5: RichResultSet => T5,
                                                                              c6: RichResultSet => T6,
                                                                              c7: RichResultSet => T7,
                                                                              c8: RichResultSet => T8,
                                                                              c9: RichResultSet => T9,
                                                                              c10: RichResultSet => T10,
                                                                              c11: RichResultSet => T11,
                                                                              c12: RichResultSet => T12,
                                                                              c13: RichResultSet => T13,
                                                                              c14: RichResultSet => T14): R = {
    apply(rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs)
  }

  def mapping[R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15]
  (rs: RichResultSet, apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) => R)(implicit c1: RichResultSet => T1,
                                                                                   c2: RichResultSet => T2,
                                                                                   c3: RichResultSet => T3,
                                                                                   c4: RichResultSet => T4,
                                                                                   c5: RichResultSet => T5,
                                                                                   c6: RichResultSet => T6,
                                                                                   c7: RichResultSet => T7,
                                                                                   c8: RichResultSet => T8,
                                                                                   c9: RichResultSet => T9,
                                                                                   c10: RichResultSet => T10,
                                                                                   c11: RichResultSet => T11,
                                                                                   c12: RichResultSet => T12,
                                                                                   c13: RichResultSet => T13,
                                                                                   c14: RichResultSet => T14,
                                                                                   c15: RichResultSet => T15): R = {
    apply(rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs)
  }

  def mapping[R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16]
  (rs: RichResultSet, apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) => R)(implicit c1: RichResultSet => T1,
                                                                                        c2: RichResultSet => T2,
                                                                                        c3: RichResultSet => T3,
                                                                                        c4: RichResultSet => T4,
                                                                                        c5: RichResultSet => T5,
                                                                                        c6: RichResultSet => T6,
                                                                                        c7: RichResultSet => T7,
                                                                                        c8: RichResultSet => T8,
                                                                                        c9: RichResultSet => T9,
                                                                                        c10: RichResultSet => T10,
                                                                                        c11: RichResultSet => T11,
                                                                                        c12: RichResultSet => T12,
                                                                                        c13: RichResultSet => T13,
                                                                                        c14: RichResultSet => T14,
                                                                                        c15: RichResultSet => T15,
                                                                                        c16: RichResultSet => T16): R = {
    apply(rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs)
  }

  def mapping[R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17]
  (rs: RichResultSet, apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) => R)(implicit c1: RichResultSet => T1,
                                                                                             c2: RichResultSet => T2,
                                                                                             c3: RichResultSet => T3,
                                                                                             c4: RichResultSet => T4,
                                                                                             c5: RichResultSet => T5,
                                                                                             c6: RichResultSet => T6,
                                                                                             c7: RichResultSet => T7,
                                                                                             c8: RichResultSet => T8,
                                                                                             c9: RichResultSet => T9,
                                                                                             c10: RichResultSet => T10,
                                                                                             c11: RichResultSet => T11,
                                                                                             c12: RichResultSet => T12,
                                                                                             c13: RichResultSet => T13,
                                                                                             c14: RichResultSet => T14,
                                                                                             c15: RichResultSet => T15,
                                                                                             c16: RichResultSet => T16,
                                                                                             c17: RichResultSet => T17): R = {
    apply(rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs)
  }

  def mapping[R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18]
  (rs: RichResultSet, apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) => R)(implicit c1: RichResultSet => T1,
                                                                                                  c2: RichResultSet => T2,
                                                                                                  c3: RichResultSet => T3,
                                                                                                  c4: RichResultSet => T4,
                                                                                                  c5: RichResultSet => T5,
                                                                                                  c6: RichResultSet => T6,
                                                                                                  c7: RichResultSet => T7,
                                                                                                  c8: RichResultSet => T8,
                                                                                                  c9: RichResultSet => T9,
                                                                                                  c10: RichResultSet => T10,
                                                                                                  c11: RichResultSet => T11,
                                                                                                  c12: RichResultSet => T12,
                                                                                                  c13: RichResultSet => T13,
                                                                                                  c14: RichResultSet => T14,
                                                                                                  c15: RichResultSet => T15,
                                                                                                  c16: RichResultSet => T16,
                                                                                                  c17: RichResultSet => T17,
                                                                                                  c18: RichResultSet => T18): R = {
    apply(rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs, rs)
  }

}
