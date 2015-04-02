package microsql
import scala.language.implicitConversions
import SQL._

/**
 * An attempt at simplifying the extracting of ResultSets to case classes.
 *
 * WIP
 */
object Extractors {

  implicit def mapping[R, T1](apply: (T1) => R)(implicit c1: RichResultSet => T1): RichResultSet => R =
    (r: RichResultSet) => apply(r)


  implicit def mapping[R, T1, T2](apply: (T1, T2) => R)(
    implicit c1: RichResultSet => T1,
    c2: RichResultSet => T2): RichResultSet => R =
    (r: RichResultSet) => apply(r, r)


  implicit def mapping[R, T1, T2, T3](apply: (T1, T2, T3) => R)(implicit c1: RichResultSet => T1,
                                                       c2: RichResultSet => T2,
                                                       c3: RichResultSet => T3): RichResultSet => R = {
    (r: RichResultSet) => apply(r, r, r)
  }

  implicit def mapping[R, T1, T2, T3, T4](apply: (T1, T2, T3, T4) => R)(implicit c1: RichResultSet => T1,
                                                               c2: RichResultSet => T2,
                                                               c3: RichResultSet => T3,
                                                               c4: RichResultSet => T4): RichResultSet => R = {
    (r: RichResultSet) => apply(r, r, r, r)
  }

  implicit def mapping[R, T1, T2, T3, T4, T5](apply: (T1, T2, T3, T4, T5) => R)(implicit c1: RichResultSet => T1,
                                                                       c2: RichResultSet => T2,
                                                                       c3: RichResultSet => T3,
                                                                       c4: RichResultSet => T4,
                                                                       c5: RichResultSet => T5): RichResultSet => R = {
    (r: RichResultSet) => apply(r, r, r, r, r)
  }

  implicit def mapping[R, T1, T2, T3, T4, T5, T6]
  (apply: (T1, T2, T3, T4, T5, T6) => R)(implicit c1: RichResultSet => T1,
                                         c2: RichResultSet => T2,
                                         c3: RichResultSet => T3,
                                         c4: RichResultSet => T4,
                                         c5: RichResultSet => T5,
                                         c6: RichResultSet => T6): RichResultSet => R = {
    (r: RichResultSet) => apply(r, r, r, r, r, r)
  }

  implicit def mapping[R, T1, T2, T3, T4, T5, T6, T7]
  (apply: (T1, T2, T3, T4, T5, T6, T7) => R)(implicit c1: RichResultSet => T1,
                                             c2: RichResultSet => T2,
                                             c3: RichResultSet => T3,
                                             c4: RichResultSet => T4,
                                             c5: RichResultSet => T5,
                                             c6: RichResultSet => T6,
                                             c7: RichResultSet => T7): RichResultSet => R = {
    (r: RichResultSet) => apply(r, r, r, r, r, r, r)
  }

  implicit def mapping[R, T1, T2, T3, T4, T5, T6, T7, T8]
  (apply: (T1, T2, T3, T4, T5, T6, T7, T8) => R)(implicit c1: RichResultSet => T1,
                                                 c2: RichResultSet => T2,
                                                 c3: RichResultSet => T3,
                                                 c4: RichResultSet => T4,
                                                 c5: RichResultSet => T5,
                                                 c6: RichResultSet => T6,
                                                 c7: RichResultSet => T7,
                                                 c8: RichResultSet => T8): RichResultSet => R = {
    (r: RichResultSet) => apply(r, r, r, r, r, r, r, r)
  }

  implicit def mapping[R, T1, T2, T3, T4, T5, T6, T7, T8, T9]
  (apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9) => R)(implicit c1: RichResultSet => T1,
                                                     c2: RichResultSet => T2,
                                                     c3: RichResultSet => T3,
                                                     c4: RichResultSet => T4,
                                                     c5: RichResultSet => T5,
                                                     c6: RichResultSet => T6,
                                                     c7: RichResultSet => T7,
                                                     c8: RichResultSet => T8,
                                                     c9: RichResultSet => T9): RichResultSet => R = {
    (r: RichResultSet) => apply(r, r, r, r, r, r, r, r, r)
  }

  implicit def mapping[R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10]
  (apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) => R)(implicit c1: RichResultSet => T1,
                                                          c2: RichResultSet => T2,
                                                          c3: RichResultSet => T3,
                                                          c4: RichResultSet => T4,
                                                          c5: RichResultSet => T5,
                                                          c6: RichResultSet => T6,
                                                          c7: RichResultSet => T7,
                                                          c8: RichResultSet => T8,
                                                          c9: RichResultSet => T9,
                                                          c10: RichResultSet => T10): RichResultSet => R = {
    (r: RichResultSet) => apply(r, r, r, r, r, r, r, r, r, r)
  }

  implicit def mapping[R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11]
  (apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) => R)(implicit c1: RichResultSet => T1,
                                                               c2: RichResultSet => T2,
                                                               c3: RichResultSet => T3,
                                                               c4: RichResultSet => T4,
                                                               c5: RichResultSet => T5,
                                                               c6: RichResultSet => T6,
                                                               c7: RichResultSet => T7,
                                                               c8: RichResultSet => T8,
                                                               c9: RichResultSet => T9,
                                                               c10: RichResultSet => T10,
                                                               c11: RichResultSet => T11): RichResultSet => R = {
    (r: RichResultSet) => apply(r, r, r, r, r, r, r, r, r, r, r)
  }

  implicit def mapping[R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12]
  (apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) => R)(implicit c1: RichResultSet => T1,
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
                                                                    c12: RichResultSet => T12): RichResultSet => R = {
    (r: RichResultSet) => apply(r, r, r, r, r, r, r, r, r, r, r, r)
  }

  implicit def mapping[R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13]
  (apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) => R)(implicit c1: RichResultSet => T1,
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
                                                                         c13: RichResultSet => T13): RichResultSet => R = {
    (r: RichResultSet) => apply(r, r, r, r, r, r, r, r, r, r, r, r, r)
  }

  implicit def mapping[R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14]
  (apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) => R)(implicit c1: RichResultSet => T1,
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
                                                                              c14: RichResultSet => T14): RichResultSet => R = {
    (r: RichResultSet) => apply(r, r, r, r, r, r, r, r, r, r, r, r, r, r)
  }

  implicit def mapping[R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15]
  (apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) => R)(implicit c1: RichResultSet => T1,
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
                                                                                   c15: RichResultSet => T15): RichResultSet => R = {
    (r: RichResultSet) => apply(r, r, r, r, r, r, r, r, r, r, r, r, r, r, r)
  }

  implicit def mapping[R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16]
  (apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) => R)(implicit c1: RichResultSet => T1,
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
                                                                                        c16: RichResultSet => T16): RichResultSet => R = {
    (r: RichResultSet) => apply(r, r, r, r, r, r, r, r, r, r, r, r, r, r, r, r)
  }

  implicit def mapping[R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17]
  (apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) => R)(implicit c1: RichResultSet => T1,
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
                                                                                             c17: RichResultSet => T17): RichResultSet => R = {
    (r: RichResultSet) => apply(r, r, r, r, r, r, r, r, r, r, r, r, r, r, r, r, r)
  }

  implicit def mapping[R, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18]
  (apply: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) => R)(implicit c1: RichResultSet => T1,
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
                                                                                                  c18: RichResultSet => T18): RichResultSet => R = {
    (r: RichResultSet) => apply(r, r, r, r, r, r, r, r, r, r, r, r, r, r, r, r, r, r)
  }

}
