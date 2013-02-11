package microsql

import org.scalatest.{FeatureSpec,GivenWhenThen}
import java.sql._

class SQLTest extends FeatureSpec with GivenWhenThen {

  feature("The user can run insert queries") {

    info("As a programmer")
    info("I want to be able to run insert queries on my database")

    scenario("running a create and insert query") {
      import microsql.SQL._

      given("an empty database")
      Class.forName("org.h2.Driver")
      implicit val c = DriverManager.getConnection("jdbc:h2:/tmp/test.db","sa","")
      executeSimple("drop table if exists student")
      executeSimple("drop table if exists teacher")
      executeSimple("create table if not exists student (id int, name varchar(128), last_name varchar(128))")

      when("an insert query is run")
      executeSimple("insert into student (id,name,last_name) values (1,'john','doe')")

      then("that row should be fetchable from the database")
      val studentName = executeForResult("select * from student where id=1") { r =>
        r.getString("name")
      }.head
      assert(studentName == "john")

      c.close()
    }

    scenario("running a batch insert statement") {
      import microsql.SQL._

      given("an empty database")
      Class.forName("org.h2.Driver")
      implicit val c = DriverManager.getConnection("jdbc:h2:/tmp/test.db","sa","")
      executeSimple("drop table if exists student")
      executeSimple("drop table if exists teacher")
      executeSimple("create table if not exists student (id int, name varchar(128), last_name varchar(128))")

      when("a batch insert statement is run")
      executeSimple("insert into student (id,name,last_name) values (?,?,?)",
        List((1,"john","doe"),
             (2,"marry","poppins"),
             (3,"peter","pan")))

      then("the table should have the inserted number of rows")
      val rowCount = executeForResult("select count(*) as count from student") {_.getInt("count")}.head
      assert(rowCount == 3)

      c.close()
    }
  }

  feature("The user can run select queries"){
    scenario("fetching resultset rows as Maps") {
      import microsql.SQL._

      given("an empty database")
      Class.forName("org.h2.Driver")
      implicit val c = DriverManager.getConnection("jdbc:h2:/tmp/test.db","sa","")
      executeSimple("drop table if exists student")
      executeSimple("drop table if exists teacher")
      executeSimple("create table if not exists student (id int, name varchar(128), last_name varchar(128))")

      when("a batch insert statement is run")
      executeSimple("insert into student (id,name,last_name) values (?,?,?)",
        List((1,"john","doe"),
             (2,"marry","poppins"),
             (3,"peter","pan")))

      then("the select with rowToMap extractor function must return rows as Maps")
      val result = executeForResult("select * from student order by id")( extractToMap )
      assert(result.head("ID")("value") match {
        case Some(v) => v equals 1
        case _       => false
      })
      assert(result.drop(1).head("NAME")("value") match {
        case Some(v) => v equals "marry"
      })
      assert(result.drop(1).head("NAME")("type") equals "VARCHAR")
      c.close()
    }

    scenario("fetching resultset rows as a case class") {
      import microsql.SQL._

      given("an empty database")
      Class.forName("org.h2.Driver")
      implicit val c = DriverManager.getConnection("jdbc:h2:/tmp/test.db","sa","")
      executeSimple("drop table if exists student")
      executeSimple("drop table if exists teacher")
      executeSimple("create table if not exists student (id int, name varchar(128), last_name varchar(128))")

      when("a batch insert statement is run")
      executeSimple("insert into student (id,name,last_name) values (?,?,?)",
        List((1,"john","doe"),
             (2,"marry","poppins"),
             (3,"peter","pan")))

      then("the select with case class extractor function must return rows as that class")
      case class Student(name: String, lastName: String)
      val result = executeForResult("select name,last_name from student"){
        r => Student(r,r)
      }
      assert(result.head.name == "john")
      assert(result.drop(2).head.lastName == "pan")
      c.close()
    }

    scenario("fetching resultset rows as a case class, with multiple bound args") {
      import microsql.SQL._

      given("an empty database")
      Class.forName("org.h2.Driver")
      implicit val c = DriverManager.getConnection("jdbc:h2:/tmp/test.db","sa","")
      executeSimple("drop table if exists student")
      executeSimple("drop table if exists teacher")
      executeSimple("create table if not exists student (id int, name varchar(128), last_name varchar(128))")

      when("a batch insert statement is run")
      executeSimple("insert into student (id,name,last_name) values (?,?,?)",
        List((1,"john","doe"),
             (2,"marry","poppins"),
             (3,"peter","pan")))

      then("the select with multiple bound args must give us a list of lists of results")
      case class Student(name: String, lastName: String)
      val result = executeForResult(
        "select name,last_name from student where name=?",
        List("john","marry","peter")
      ){ r => Student(r,r)}
      assert(result.head.head.name == "john")
      assert(result.drop(2).head.head.lastName == "pan")
      c.close()
    }

    scenario("fetching resultset rows as an ResultSet iterator") {
      import microsql.SQL._

      given("an empty database")
      Class.forName("org.h2.Driver")
      implicit val c = DriverManager.getConnection("jdbc:h2:/tmp/test.db","sa","")
      executeSimple("drop table if exists student")
      executeSimple("drop table if exists teacher")
      executeSimple("create table if not exists student (id int, name varchar(128), last_name varchar(128))")

      when("a batch insert statement is run")
      executeSimple("insert into student (id,name,last_name) values (?,?,?)",
        List((1,"john","doe"),
             (2,"marry","poppins"),
             (3,"peter","pan")))

      then("the select with multiple bound args must give us a list of lists of results")
      case class Student(name: String, lastName: String)
      val result =
        ("select name,last_name from student where name=?" << "thedog" execute).map{ rs =>
          rs.getString("name")
        }.toList.headOption

      c.close()
    }

  }

  feature("The user can run transactional queries"){
    scenario("running a successful transaction") {
      import microsql.SQL._

      given("an empty database")
      Class.forName("org.h2.Driver")
      implicit val c = DriverManager.getConnection("jdbc:h2:/tmp/test.db","sa","")
      executeSimple("drop table if exists student")
      executeSimple("drop table if exists teacher")
      executeSimple("create table if not exists student (id int, name varchar(128), last_name varchar(128))")

      when("a batch insert statement is run")
      executeSimple("insert into student (id,name,last_name) values (?,?,?)",
        List((1,"john","doe"),
             (2,"marry","poppins"),
             (3,"peter","pan")))
      
      when("an insert is made in a transaction")
      transaction {
        executeSimple("insert into student (id,name,last_name) values (?,?,?)",
          List((4,"some","one")))
      }
      then("the new row is visible after a transaction is committed")
      val result = 
        executeForResult("select name from student where id=4")( _.getString("name")).head
      assert(result == "some")
      c.close()
    }

    scenario("running a transaction that rolls back") {
      import microsql.SQL._

      given("an empty database")
      Class.forName("org.h2.Driver")
      implicit val c = DriverManager.getConnection("jdbc:h2:/tmp/test.db","sa","")
      executeSimple("drop table if exists student")
      executeSimple("drop table if exists teacher")
      executeSimple("create table if not exists student (id int, name varchar(128), last_name varchar(128))")

      when("a batch insert statement is run")
      executeSimple("insert into student (id,name,last_name) values (?,?,?)",
        List((1,"john","doe"),
             (2,"marry","poppins"),
             (3,"peter","pan")))
      
      when("an insert is made in a transaction")
      val transactionResult = transaction {
        executeSimple("insert into student (id,name,last_name) values (?,?,?)",
          List((4,"some","one")))
        
        when("an exception is thrown")
        throw new Exception("rollback this")
      }

      then("the result of a rolled back transaction is a Left[X] and the changes are undone")
      val result = 
        executeForResult("select name from student where id=4")( _.getString("name")).headOption
      assert(transactionResult.isLeft == true)
      assert(result.isEmpty == true)
      c.close()
    }
  }
}
