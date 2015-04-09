package microsql
import scala.language.postfixOps

import org.scalatest.{FeatureSpec, GivenWhenThen}
import java.sql._

class SQLTest extends FeatureSpec with GivenWhenThen {

  feature("The user can run insert queries") {

    info("As a programmer")
    info("I want to be able to run insert queries on my database")

    scenario("running a create and insert query") {
      import microsql.SQL._

      Given("an empty database")
      Class.forName("org.h2.Driver")
      implicit val c = DriverManager.getConnection("jdbc:h2:/tmp/test.db","sa","")
      executeSimple("drop table if exists student")
      executeSimple("drop table if exists teacher")
      executeSimple(
        """create table if not exists
          |student (id int, name varchar(128), last_name varchar(128))
          |""".stripMargin)

      When("an insert query is run")
      executeSimple("insert into student (id,name,last_name) values (1,'john','doe')")

      Then("that row should be fetchable from the database")
      val studentName = executeForResult("select * from student where id=1") { r =>
        r.getString("name")
      }.toVector.head
      assert(studentName == "john")

      c.close()
    }

    scenario("running a batch insert statement") {
      import microsql.SQL._

      Given("an empty database")
      Class.forName("org.h2.Driver")
      implicit val c = DriverManager.getConnection("jdbc:h2:/tmp/test.db","sa","")
      executeSimple("drop table if exists student")
      executeSimple("drop table if exists teacher")
      executeSimple("create table if not exists student (id int, name varchar(128), last_name varchar(128))")

      When("a batch insert statement is run")
      executeSimple("insert into student (id,name,last_name) values (?,?,?)",
        List((1,"john","doe"),
             (2,"marry","poppins"),
             (3,"peter","pan")))

      Then("the table should have the inserted number of rows")
      val rowCount = executeForResult("select count(*) as count from student") {
        _.getInt("count")
      }.toVector.head
      assert(rowCount == 3)

      c.close()
    }
  }

  feature("The user can run select queries"){
    scenario("fetching resultset rows as a case class") {
      import microsql.SQL._

      Given("an empty database")
      Class.forName("org.h2.Driver")
      implicit val c = DriverManager.getConnection("jdbc:h2:/tmp/test.db","sa","")
      executeSimple("drop table if exists student")
      executeSimple("drop table if exists teacher")
      executeSimple("create table if not exists student (id int, name varchar(128), last_name varchar(128))")

      When("a batch insert statement is run")
      executeSimple("insert into student (id,name,last_name) values (?,?,?)",
        List((1,"john","doe"),
             (2,"marry","poppins"),
             (3,"peter","pan")))

      Then("the select with case class extractor function must return rows as that class")
      case class Student(name: String, lastName: String)
      val result = executeForResult("select name,last_name from student"){
        r => Student(r,r)
      }.toVector
      assert(result.head.name == "john")
      assert(result.drop(2).head.lastName == "pan")
      c.close()
    }

    scenario("fetching resultset rows as an ResultSet iterator") {
      import microsql.SQL._

      Given("an empty database")
      Class.forName("org.h2.Driver")
      implicit val c = DriverManager.getConnection("jdbc:h2:/tmp/test.db","sa","")
      executeSimple("drop table if exists student")
      executeSimple("drop table if exists teacher")
      executeSimple("create table if not exists student (id int, name varchar(128), last_name varchar(128))")

      When("a batch insert statement is run")
      executeSimple("insert into student (id,name,last_name) values (?,?,?)",
        List((1,"john","doe"),
             (2,"marry","poppins"),
             (3,"peter","pan")))

      Then("the select with multiple bound args must give us a list of lists of results")
      case class Student(name: String, lastName: String)
      val studentQ: RichPreparedStatement = "select name,last_name from student where name=?"
      val result =
        studentQ("thedog").execute[String] { rs =>
          rs.getString("name")
        }.toList.headOption

      c.close()
    }

  }

  feature("The user can run transactional queries"){
    scenario("running a successful transaction") {
      import microsql.SQL._

      Given("an empty database")
      Class.forName("org.h2.Driver")
      implicit val c = DriverManager.getConnection("jdbc:h2:/tmp/test.db","sa","")
      executeSimple("drop table if exists student")
      executeSimple("drop table if exists teacher")
      executeSimple("create table if not exists student (id int, name varchar(128), last_name varchar(128))")

      When("a batch insert statement is run")
      executeSimple("insert into student (id,name,last_name) values (?,?,?)",
        List((1,"john","doe"),
             (2,"marry","poppins"),
             (3,"peter","pan")))
      
      When("an insert is made in a transaction")
      transaction {
        executeSimple("insert into student (id,name,last_name) values (?,?,?)",
          List((4,"some","one")))
      }
      Then("the new row is visible after a transaction is committed")
      val result = 
        executeForResult("select name from student where id=4"){
          _.getString("name")
        }.toVector.head
      assert(result == "some")

      c.close()
    }

    scenario("running a transaction that rolls back") {
      import microsql.SQL._

      Given("an empty database")
      Class.forName("org.h2.Driver")
      implicit val c = DriverManager.getConnection("jdbc:h2:/tmp/test.db","sa","")
      executeSimple("drop table if exists student")
      executeSimple("drop table if exists teacher")
      executeSimple("create table if not exists student (id int, name varchar(128), last_name varchar(128))")

      When("a batch insert statement is run")
      executeSimple("insert into student (id,name,last_name) values (?,?,?)",
        List((1,"john","doe"),
             (2,"marry","poppins"),
             (3,"peter","pan")))
      
      When("an insert is made in a transaction")
      val transactionResult = transaction {
        executeSimple("insert into student (id,name,last_name) values (?,?,?)",
          List((4,"some","one")))
        
        When("an exception is thrown")
        throw new Exception("rollback this")
      }

      Then("the result of a rolled back transaction is a Left[X] and the changes are undone")
      val result = 
        executeForResult("select name from student where id=4"){
          _.getString("name")
        }.toVector.headOption
      assert(transactionResult.isLeft)
      assert(result.isEmpty)
      c.close()
    }

    scenario("Extractors work with case class apply methods") {
      import microsql.SQL._
      import microsql.Extractors._

      Given("an empty database")
      Class.forName("org.h2.Driver")
      implicit val c = DriverManager.getConnection("jdbc:h2:/tmp/test.db","sa","")
      executeSimple("drop table if exists student")
      executeSimple("drop table if exists teacher")
      executeSimple("create table if not exists student (id int, name varchar(128), last_name varchar(128))")

      When("a batch insert statement is run")
      executeSimple("insert into student (id,name,last_name) values (?,?,?)",
        List((1,"john","doe"),
          (2,"marry","poppins"),
          (3,"peter","pan")))

      Then("Extractors work with case classes")
      case class TestQResult(name: String, last_name: String)
      val result = executeForResult("select name, last_name from student order by id")(TestQResult.apply)
      result.foreach{r =>
        assert(r.name == "john" || r.name == "marry" || r.name == "peter")
      }

    }
  }
}
