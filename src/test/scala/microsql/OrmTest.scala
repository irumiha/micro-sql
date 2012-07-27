package microsql

import org.scalatest.{FeatureSpec,GivenWhenThen}
import java.sql._
import microsql.Orm._

case class Student(var name: String, var last_name: String) extends LongIDKey
case class Teacher(var name: String, var last_name: String, id: Long)

class OrmTest extends FeatureSpec with GivenWhenThen {

  feature("The user can use really simple ORM-like helpers") {
    info("As a programmer")
    info("I want to be able to run simple ORM-like operations on the domain model")

    scenario("create a new object") {
      import microsql.SQL._

      given("an empty database with a student table")
      Class.forName("org.h2.Driver")
      implicit val c = DriverManager.getConnection("jdbc:h2:/tmp/testdb2.db","sa","")
      executeSimple("drop table if exists student")
      executeSimple("drop table if exists teacher")

      executeSimple("create table student (name varchar(128), last_name varchar(128),id identity)")
      executeSimple("create table teacher (name varchar(128), last_name varchar(128),id identity)")

      when("a test row is inserted")
      withPrepared("insert into student (name,last_name) values ('john','doe')",returnGeneratedKeys){ s =>
        val genKey = s.execute.getGeneratedKeys
        if (genKey.next)
          println("generated key: " + genKey)
        val genKey2 = s.execute.getGeneratedKeys
        if (genKey2.next)
          println("generated key: " + genKey2)
      }

      when("a domain model is defined")
      object SchoolSchema extends Schema {
        val entities = Map(
          entity[Student]("student"),
          entity[Teacher]("teacher"))
      }

      then("a simple case class extractor should work")
      import SchoolSchema._

      val r = executeForResult("select * from student where id=1")( extract[Student](_) ).head
      assert(r.isInstanceOf[Student])
      assert(r.name == "john")
      assert(r.id == 1)

      println(insert(Student("igor","rumiha")).id)
      "select * from student" execute ( extract[Student](_) ) foreach (s => println(s.toString + " with id: " + s.id))
      println(loadByID[Student](1))
      println(loadByID[Student](32))
      val testUpdate = loadByID[Student](1).get
      testUpdate.last_name = "somethingother"
      update(testUpdate)
      assert(loadByID[Student](1).get.last_name equals "somethingother")
    }
  }
}