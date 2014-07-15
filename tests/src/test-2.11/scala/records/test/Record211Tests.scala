package records.test

import org.scalatest._

import records.R

class Record211Tests extends FlatSpec with Matchers {

  case class DBRecord(name: String, age: Int, location: String)

  "A Record" should "not depend on declared field order" in {

    val people = List(
      R("age" -> 1, "name" -> "Michael"),
      R("name" -> "Ahir", "age" -> 23))

    people.head.name should be ("Michael")
    people.last.name should be ("Ahir")

  }

  it should "be able to convert to complex case classes" in {

    val data = List(
      R("name" -> "Hans",  "age" -> 256, "location" -> "home"),
      R("name" -> "Peter", "age" ->   1, "location" -> "bed"),
      R("name" -> "Chuck", "age" ->   2, "location" -> "bar"))

    val recs = data.map(_.to[DBRecord])

    recs should be (List(
      DBRecord("Hans",  256, "home"),
      DBRecord("Peter",   1,  "bed"),
      DBRecord("Chuck",   2,  "bar")))
  }

  it should "lub for records of different shapes" in {

    val data = List(
      R("name" -> "Hans"),
      R("name" -> "Peter", "age" ->   1),
      R("name" -> "Chuck", "age" ->   2, "location" -> "bar"))

    val recs = data.map(_.name)

    recs should be (List("Hans", "Peter", "Chuck"))
  }

  it should "lub different records in different contexts" in {
    val x = List(R("age" -> 2, "name" -> "Heather"), R("age" -> 3, "name" -> "Tobias"))

    x.head.age should be (2)
    x.last.age should be (3)

    val r = if (true) R("age" -> 2, "name" -> "Tobias") else R("age" -> 1, "name" -> "Heather")
    r.age should be (2)

    val r1 = true match {
      case true => R("age" -> 3, "name" -> "Hubert")
      case false => R("age" -> 3, "name" -> "Hubert")
    }
    r1.age should be (3)
  }

}