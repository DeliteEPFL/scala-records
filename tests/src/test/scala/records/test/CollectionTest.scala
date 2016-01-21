package records.test

import org.scalatest._

import Inspectors._
import Assertions._

import records.Record

import scala.collection.mutable

// This is for 2.10.x compatibility!
import scala.language.reflectiveCalls

class CollectionTest extends FlatSpec with Matchers {

  "A record" should "be tabulated in a list" in {
    val n = 10
    val l = List.tabulate(n)(i => Record("inc" -> i, "dec" -> (n - i)))

    forAll(l) { x => (x.inc + x.dec) should be(n) }
  }

  it should "work in untyped sets" in {

    val set = mutable.Set.empty[Any]

    set += Record("a" -> 1)
    set += Record("b" -> 2)
    set += Record("Hello World" -> 5, "r" -> Record("b" -> 3))

    assert(set.contains(Record("a" -> 1)))
    assert(set.contains(Record("b" -> 2)))
    assert(set.contains(Record("Hello World" -> 5, "r" -> Record("b" -> 3))))

    assert(!set.contains("String"))
    assert(!set.contains(Record("a" -> 5)))
    assert(!set.contains(Record("c" -> 1)))
  }

  it should "be used as key in a map" in {
    val map = Map(Record("a" -> 1) -> "hello", Record("a" -> 2) -> "world")

    map(Record("a" -> 1)) should be("hello")
    map(Record("a" -> 2)) should be("world")

    assert(!map.contains(Record("a" -> 5)))
    assert(!map.contains(Record("a" -> 3)))
  }

  it should "be used as key in an untyped map" in {
    val map = mutable.Map.empty[Any, Int]

    map += "Foo" -> 5
    map += Record("foo" -> "foo") -> 10
    map += Record("foo" -> Record("bar" -> "a")) -> 100

    map("Foo") should be(5)
    map(Record("foo" -> "foo")) should be(10)
    map(Record("foo" -> Record("bar" -> "a"))) should be(100)

    assert(!map.contains(1))
    assert(!map.contains("Bar"))
    assert(!map.contains(Record("bar" -> "foo")))
    assert(!map.contains(Record("foo" -> "bar")))
  }

  it should "work in scala.Array" in {
    val array = Array(Record("-" -> 1, "+" -> -1), Record("-" -> 2, "+" -> -2))

    array.size should be(2)

    array(0) should be(Record("-" -> 1, "+" -> -1))
    array(1) should be(Record("-" -> 2, "+" -> -2))
  }

}
