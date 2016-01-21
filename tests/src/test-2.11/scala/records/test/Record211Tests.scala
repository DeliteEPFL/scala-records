package records.test

import org.scalatest._

import records.Record

import scala.collection.mutable

class Record211Tests extends FlatSpec with Matchers {

  case class DBRecord(name: String, age: Int, location: String)

  "A Record" should "not depend on declared field order" in {

    val people = List(
      Record("age" -> 1, "name" -> "Michael"),
      Record("name" -> "Ahir", "age" -> 23))

    people.head.name should be ("Michael")
    people.last.name should be ("Ahir")

  }

  it should "be able to convert to complex case classes" in {

    val data = List(
      Record("name" -> "Hans",  "age" -> 256, "location" -> "home"),
      Record("name" -> "Peter", "age" ->   1, "location" -> "bed"),
      Record("name" -> "Chuck", "age" ->   2, "location" -> "bar"))

    val recs = data.map(_.to[DBRecord])

    recs should be (List(
      DBRecord("Hans",  256, "home"),
      DBRecord("Peter",   1,  "bed"),
      DBRecord("Chuck",   2,  "bar")))
  }

  it should "lub for records of different shapes" in {

    val data = List(
      Record("name" -> "Hans"),
      Record("name" -> "Peter", "age" ->   1),
      Record("name" -> "Chuck", "age" ->   2, "location" -> "bar"))

    val recs = data.map(_.name)

    recs should be (List("Hans", "Peter", "Chuck"))
  }

  it should "lub different records in different contexts" in {
    val x = List(Record("age" -> 2, "name" -> "Heather"), Record("age" -> 3, "name" -> "Tobias"))

    x.head.age should be (2)
    x.last.age should be (3)

    val r = if (true) Record("age" -> 2, "name" -> "Tobias") else Record("age" -> 1, "name" -> "Heather")
    r.age should be (2)

    val r1 = true match {
      case true => Record("age" -> 3, "name" -> "Hubert")
      case false => Record("age" -> 3, "name" -> "Hubert")
    }
    r1.age should be (3)
  }

  it should "work in implicitly typed sets" in {

    val set = mutable.Set(Record("a" -> 1, "b" -> 2))

    set += Record("a" -> 2, "b" -> 3)
    set += Record("a" -> 5, "b" -> 3)

    assert(set.contains(Record("a" -> 1, "b" -> 2)))
    assert(set.contains(Record("a" -> 2, "b" -> 3)))
    assert(set.contains(Record("a" -> 5, "b" -> 3)))

    assert(!set.contains(Record("a" -> 7, "b" -> 9)))
    assert(!set.contains(Record("a" -> 7, "b" -> 10)))
  }

}
