package records.test

import org.scalatest._

// This is for 2.10.x compatibility!
import scala.language.reflectiveCalls

class BasicTest extends FlatSpec with Matchers {

  def defRecord(age: Int = 2) = records.Record(
    "age" -> age,
    "name" -> "David")

  "A Record" should "allow to read the value directly" in {
    val record = defRecord()

    record.age should be(2)
  }

  it should "be created with a special constructor" in {
    val row = records.Record("foo" -> 1, ("bar", 2.3), Tuple2("baz", 1.7))

    row.foo should be(1)
    row.bar should be(2.3)
    row.baz should be(1.7)
  }

  it should "allow renaming in imports" in {
    import records.{ Record => X }
    val row = X("foo" -> 1)

    row.foo should be(1)
  }

  it should "allow aliases" in {
    val X = records.Record
    val row = X("foo" -> 1)

    row.foo should be(1)
  }

  it should "be hygienic" in {
    object records {
      val Rec = Predef
    }
    defRecord(3).age should be(3)
  }

  import records.Record
  it should "allow strange field names" in {
    val record = Record(
      "type" -> "R",
      "blank space" -> "blank space",
      "1" -> 1,
      "1>2" -> "1>2",
      "豆贝尔维" -> "dòu bèi ěr wéi")

    record.`type` should be("R")
    record.`blank space` should be("blank space")
    record.`1` should be(1)
    record.`1>2` should be("1>2")
    record.`豆贝尔维` should be("dòu bèi ěr wéi")
  }

  it should "allow to read the value in a closure" in {
    val record = defRecord()

    val f = () => record.name

    f() should be("David")
  }

  it should "allow Rows as a result in a method type" in {

    def query = defRecord()

    query.age should be(2)

  }

  it should "allow rows in generics" in {
    import language.existentials

    class Box[T](val x: T) {
      def get = x
    }

    val x = new Box(defRecord())

    x.get.age should be(2)

  }

  it should "allow to fill lists" in {
    val x = List.fill(1)(defRecord())

    x.head.age should be(2)
  }

  it should "LUB properly if both records are the same" in {
    val x = List(defRecord(), defRecord(3))

    x.head.age should be(2)
    x.last.age should be(3)

    val r = if (true) defRecord() else defRecord(3)
    val r1 = true match {
      case true  => defRecord(3)
      case false => defRecord()
    }
    r.age should be(2)
    r1.age should be(3)
  }

  // Records have a curse that they can never be seen so
  // explicit mentions of records must be defined as case classes.
  case class AgeName(age: Int, name: String)

  it should "allow different valued rows in ascribed lists" in {
    val x = List[AgeName](defRecord().to[AgeName], defRecord(3).to[AgeName])
    x.head.age should be(2)
    x.last.age should be(3)
  }

  it should "allow to ascribe a result type" in {

    def query: AgeName =
      defRecord().to[AgeName]

    query.age should be(2)
  }

  it should "allow tuples to construct literal rows" in {

    val row = Record(("foo", 1), ("bar", 2.3), Tuple2("baz", 1.7))

    row.foo should be(1)
    row.bar should be(2.3)
    row.baz should be(1.7)
  }

  it should "allow nested records" in {

    val x = Record("a" -> Record("b" -> 1))

    val y = Record("a" -> Record("b" -> Record("c" -> 1)))

    x.a.b should be(1)

    val v: Int = x.a.b
  }

  it should "provide toString" in {
    class A { override def toString = "[A: my String]" }

    val row = Record("foo" -> "Hello World", "blah" -> 1, "a" -> new A)

    row.toString should be("Rec { foo = Hello World, blah = 1, a = [A: my String] }")
  }

  it should "provide hashCode" in {
    val a = Record("a" -> 1, "b" -> "Hello World")
    val b = Record("a" -> 1.0, "b" -> "Hello World")
    val c = Record("a" -> 1, "b" -> "Hello Werld")

    a.hashCode should be(b.hashCode)
    a.hashCode should not be (c.hashCode)
  }

  it should "provide __dataCount" in {
    val a = Record()
    val b = Record("a" -> 1)
    val c = Record("c" -> 2, "d" -> 3, "e" -> 5)

    a.__dataCount should be(0)
    b.__dataCount should be(1)
    c.__dataCount should be(3)
  }

  it should "provide __dataAny" in {
    val a = Record("c" -> 2, "d" -> 3, "e" -> "bar")

    a.__dataAny("c") should be(2)
    a.__dataAny("d") should be(3)
    a.__dataAny("e") should be("bar")
  }

  it should "provide __dataExists" in {
    val a = Record("a" -> 4, "Hello World" -> "foo")

    a.__dataExists("a") should be(true)
    a.__dataExists("Hello World") should be(true)
    a.__dataExists("bar") should be(false)
  }

  it should "provide equals" in {
    val a = Record("a" -> 1, "b" -> "Hello World")
    val b = Record("a" -> 1.0, "b" -> "Hello World")
    val c = Record("a" -> 1, "b" -> "Hello Werld")

    a should be(b)
    a should not be (c)
    b should not be (c)
  }

  it should "provide equals for nested records" in {

    val a = Record("a" -> 1, "b" -> Record("a" -> "foo", "bar" -> "bar"))
    val b = Record("b" -> Record("a" -> "foo", "bar" -> "bar"), "a" -> 1)
    val c = Record("b" -> Record("a" -> "foo2", "bar" -> "bar"), "a" -> 1)

    a should be(b)
    a should not be (c)
    b should not be (c)
  }

  it should "support Record(a = 1) syntax" in {
    val r = Record(a = "foo", b = 1234)

    r.a should be("foo")
    r.b should be(1234)
  }

  it should "support generic fields" in {
    val r = Record(a = List(1, 2, 3))

    r.a should be(List(1, 2, 3))
  }

  it should "support array fields" in {
    val r = Record(a = "foo", b = Array(1, 2, 3))

    r.a should be("foo")
    r.b should be(Array(1, 2, 3))
  }

  case class Box[T](x: T)
  it should "be able to nest wrapped records" in {
    val x = Record("theBox" -> Box(Record("a" -> 1, "b" -> Box("asdf"))))

    x.theBox.x.a should be(1)
  }

  it should "allow to write types" in {
    val x: Record[{ def name: String; def age: Int }] = Record(name = "foo", age = 1)

    x.name should be("foo")
    x.age should be(1)
  }
}
