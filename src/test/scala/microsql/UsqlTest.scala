package microsql

import org.scalatest.{FeatureSpec,GivenWhenThen}
import java.sql._

class UsqlTest extends FeatureSpec with GivenWhenThen {

  feature("The user can run a simple insert query") {

    info("As a programmer")
    info("I want to be able to run simple queries on my database")

    scenario("running a create and insert query") {
      import microsql.Usql._

      given("an empty database")
      Class.forName("org.h2.Driver")
      implicit val c = DriverManager.getConnection("jdbc:h2:/tmp/test.db","sa","")
      executeSimple("drop table if exists student")

      when("a create table command is run")
      executeSimple("create table student (id int, name varchar(128), last_name varchar(128))")

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
      import microsql.Usql._

      given("an empty database")
      Class.forName("org.h2.Driver")
      implicit val c = DriverManager.getConnection("jdbc:h2:/tmp/test.db","sa","")
      executeSimple("drop table if exists student")

      when("a create table command is run")
      executeSimple("create table student (id int, name varchar(128), last_name varchar(128))")

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
}
