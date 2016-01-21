package records.test

import org.scalatest._

import records.Record

class UnapplyTest extends FlatSpec with Matchers {
  "A record" should "extract fields with corresponding types" in {
    val Record(x, y) = Record(x = 1, y = 2)
    val xx: Int = x
    val yy: Int = y
  }

  it should "be able to extract fields that are not in the signature" in {
    val rec: Record[_] = Record(x = 1, y = 2)
    val Record(x, y) = rec
    val Record(("x", _), ("y", _)) = rec
  }

  it should "match any record with empty list of subpatterns" in {
    val Record() = Record(x = 1)
  }

  it should "match with subpatterns within binders" in {
    val Record(x @ 1, y @ 2) = Record(x = 1, y = 2)
    val Record(("x", 1), ("y", 2)) = Record(x = 1, y = 2)
  }

  it should "fail to match if not all required fields are present" in {
    an[MatchError] should be thrownBy {
      val Record(x, y) = Record()
    }
  }

  it should "extract fields with unicode name" in {
    val rec = Record(`π` = 3.14)
    val Record(π) = rec
    val Record(("π", 3.14)) = rec
  }

  it should "interact nicely with type patterns" in {
    val Record(x: Int) = Record(x = 1)
  }

  it should "be able to cross-match records with the same field name" in {
    val p1 = Record(x = 1, y = 2)
    val p2 = Record(x = 2, y = 2)
    (p1, p2) match {
      case (Record(("y", y1)), Record(("y", y2))) if y1 == y2 =>
    }
  }
}
