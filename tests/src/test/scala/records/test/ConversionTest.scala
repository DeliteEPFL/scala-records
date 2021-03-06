package records.test

import org.scalatest._

import records.Record

// This is for 2.10.x compatibility!
import scala.language.reflectiveCalls

class ConversionTests extends FlatSpec with Matchers {

  case class SimpleVal(a: Int)
  case class ObjectVal(myObject: AnyRef)
  case class DBRecord(name: String, age: Int, location: String)
  case class DBRecordHolder(f: DBRecord, anything: Any)

  "A Record" should "be able to convert into a case class" in {
    val x = Record("a" -> 1)
    val y = x.to[SimpleVal]

    y.a should be(1)
  }

  it should "be able to convert to looser case classes" in {
    val x = Record("myObject" -> "String")
    val y = x.to[ObjectVal]

    y.myObject should be("String")
  }

  it should "be able to convert to narrower case classes" in {
    val x = Record("myObject" -> "String", "foo" -> "bar")
    val y = x.to[ObjectVal]

    y.myObject should be("String")
  }

  it should "allow conversion if there is a `to` field" in {
    val record = Record("to" -> "R")
    case class ToHolder(to: String)

    Record.fld(record).to should be("R")
    Record.ops(record).to[ToHolder] should be(ToHolder("R"))
  }

  import records.RecordConversions._
  it should "allow explicit conversion even when implicit conversion is imported" in {
    val record = Record("field" -> "42")
    case class FieldHolder(field: String)

    record.to[FieldHolder] should be(FieldHolder("42"))
  }

  it should "implicitly convert to a case class in a val position" in {
    val x: DBRecord = Record("name" -> "David", "age" -> 3, "location" -> "Lausanne")

    x.name should be("David")
  }

  it should "implicitly convert to a case class when constructing a list" in {
    val xs = List[DBRecord](
      Record("name" -> "David", "age" -> 2, "location" -> "Lausanne"),
      Record("name" -> "David", "age" -> 3, "location" -> "Lausanne"))

    xs.head.name should be("David")
    xs.tail.head.name should be("David")
  }

  it should "with nested records explicitly convert to a case class" in {
    val rec = Record("f" -> Record("name" -> "David", "age" -> 2, "location" -> "Lausanne"),
      "anything" -> 1)

    rec.to[DBRecordHolder].f.name should be("David")
  }

  it should "with nested records implicitly convert to a case class" in {
    val xs = List[DBRecordHolder](
      Record("f" -> Record("name" -> "David", "age" -> 2, "location" -> "Lausanne"),
        "anything" -> 1),
      Record("f" -> Record("name" -> "David", "age" -> 3, "location" -> "Lausanne"),
        "anything" -> 1))

    xs.head.f.name should be("David")
    xs.tail.head.f.name should be("David")
  }

  it should "be able to convert to generic case class with explicit type arguments" in {
    case class GenericDBRecord[T](name: String, age: T)
    val record = Record("name" -> "David", "age" -> 1)
    record.to[GenericDBRecord[Int]]
  }

  it should "allow conversions directly on a constructed type" in {
    case class FieldHolder(f: Int)
    Record("f" -> 1).to[FieldHolder].f should be(1)
  }
}
