package microsql
import scala.language.implicitConversions

import java.sql._
import java.io.InputStream
import scala.collection.Iterator


/**
 * Like several other libs, this one is also inspired and based on the article
 * and code at:
 *
 * <a href="https://wiki.scala-lang.org/display/SYGN/Simplifying-jdbc">https://wiki.scala-lang.org/display/SYGN/Simplifying-jdbc</a>
 * 
 */
object SQL {

  /**
   * Extracts query result row into a Map of
   * name: String -> (type: Int, value: Option[AnyRef]). This is more of a
   * homage to database libraries written in dynamic languages.
   *
   * The Int representing the type is a constant from java.sql.Types
   *
   * All column names are converted to lowercase.
   *
   * @param rrs A Rich ResultSet set to the current row
   * @return
   */
  def extractToMap(rrs: RichResultSet): Map[String,(Int, Option[AnyRef])] = {
    val md = rrs.rs.getMetaData
    val colCount = md.getColumnCount

    val nm = collection.mutable.Map[String,(Int, Option[AnyRef])]()
    var cc = 1
    while(cc <= colCount) {
      val v = rrs.rs.getObject(cc)
      nm += (md.getColumnName(cc).toLowerCase -> (md.getColumnType(cc), Option(v)))
      cc += 1
    }

    nm.toMap
  }

  /**
   * A wrapper around ResultSet that makes it look like an Iterator[ResultSet]
   * Makes iterating and extracting values cleaner.
   *
   * @param rs The wrapped ResultSet
   */
  class RSIter(val rs: ResultSet) extends Iterator[ResultSet] {
    var nextAvailable = rs.next
    var needToCallNext = false

    override def hasNext = {
      if (needToCallNext) {
        nextAvailable = rs.next()
        if (!nextAvailable) {
          rs.close()
        }
      }
      nextAvailable
    }

    override def next() = {
      needToCallNext = true
      rs
    }
  }

  /**
   * A shortcut to convert a prepared statement to an iterator of results
   *
   * @param rps The prepared and already EXECUTED statement.
   * @return
   */
  implicit def rpsToIter(rps: RichPreparedStatement): Iterator[ResultSet] = {
    val rs = rps.ps.getResultSet

    if   (rs.next) new RSIter(rs)
    else           Iterator.empty
  }

  /**
   * A shortcut to convert a SQL query string to an iterator of extracted values
   *
   * @param s The SQL query string
   * @param f Extractor function
   * @param stat sql Statement object. Intended to be used from implicit scope
   * @tparam X The extracted datastructure type
   * @return
   */
  implicit def query[X](s: String, f: RichResultSet => X)(implicit stat: Statement): Iterator[X] =
    new RSIter(stat.executeQuery(s)).map(row => f(new RichResultSet(row)))


  implicit def conn2Statement(conn: Connection): Statement = conn.createStatement

  implicit def rrs2Boolean(rs: RichResultSet): Boolean          = rs.nextBoolean
  implicit def rrs2Byte(rs: RichResultSet): Byte                = rs.nextByte
  implicit def rrs2Int(rs: RichResultSet): Int                  = rs.nextInt
  implicit def rrs2Long(rs: RichResultSet): Long                = rs.nextLong
  implicit def rrs2Float(rs: RichResultSet): Float              = rs.nextFloat
  implicit def rrs2Double(rs: RichResultSet): Double            = rs.nextDouble
  implicit def rrs2String(rs: RichResultSet): String            = rs.nextString
  implicit def rrs2Date(rs: RichResultSet): Date                = rs.nextDate
  implicit def rrs2Time(rs: RichResultSet): Time                = rs.nextTime
  implicit def rrs2Timestamp(rs: RichResultSet): Timestamp      = rs.nextTimestamp
  implicit def rrs2BinaryStream(rs: RichResultSet): InputStream = rs.nextBinStream
  implicit def rrs2Option[T](rs: RichResultSet): Option[T]      = rs.nextNullable

  implicit def resultSet2Rich(rs: ResultSet): RichResultSet = new RichResultSet(rs)
  implicit def rich2ResultSet(r: RichResultSet): ResultSet = r.rs

  // will convert any single value to a Tuple1 where it is needed
  implicit def value2tuple[T](x: T): Tuple1[T] = Tuple1(x)


  /**
   * A wrapper over the standard ResultSet that allows us to extract
   * column values using the implicit conversions defined above.
   *
   * @param rs The wrapped ResultSet
   */
  class RichResultSet(val rs: ResultSet) {

    var pos = 1
    def apply(i: Int) = { pos = i; this }

    def nextBoolean: Boolean       = { val ret = rs.getBoolean(pos);      pos = pos + 1; ret }
    def nextByte: Byte             = { val ret = rs.getByte(pos);         pos = pos + 1; ret }
    def nextInt: Int               = { val ret = rs.getInt(pos);          pos = pos + 1; ret }
    def nextLong: Long             = { val ret = rs.getLong(pos);         pos = pos + 1; ret }
    def nextFloat: Float           = { val ret = rs.getFloat(pos);        pos = pos + 1; ret }
    def nextDouble: Double         = { val ret = rs.getDouble(pos);       pos = pos + 1; ret }
    def nextString: String         = { val ret = rs.getString(pos);       pos = pos + 1; ret }
    def nextDate: Date             = { val ret = rs.getDate(pos);         pos = pos + 1; ret }
    def nextTime: Time             = { val ret = rs.getTime(pos);         pos = pos + 1; ret }
    def nextTimestamp: Timestamp   = { val ret = rs.getTimestamp(pos);    pos = pos + 1; ret }
    def nextBinStream: InputStream = { val ret = rs.getBinaryStream(pos); pos = pos + 1; ret }
    def nextNullable[AnyRef]: Option[AnyRef] = {
      val ret = Option(rs.getObject(pos).asInstanceOf[AnyRef])
      pos = pos + 1

      ret
    }
  }

  implicit def ps2Rich(ps: PreparedStatement): RichPreparedStatement = new RichPreparedStatement(ps)
  implicit def rich2PS(r: RichPreparedStatement): PreparedStatement  = r.ps

  implicit def str2RichPrepared(s: String)(implicit conn: Connection): RichPreparedStatement =
    conn.prepareStatement(s)

  /**
   * Run a code block with a prepared statement from a query string. The code
   * block should set any placeholder values, execute the statement, then
   * extract all results. Do not return a ResultSet or the received prepared
   * statement, as the prepared statement will be closed after the code block!
   *
   * @param query The SQL query string
   * @param returnID Should the result set contain generated keys?
   * @param f Extractor function
   * @param conn Implicit database connection
   * @tparam X The extracted row type
   * @return
   */
  def withPrepared[X](query: String, returnID: Boolean = false)(f: RichPreparedStatement => X)(implicit conn: Connection): X = {
    val rps: RichPreparedStatement =
      if (returnID)
        conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
      else
        conn.prepareStatement(query)

    val r = f(rps)
    rps.close()
    r
  }

  /**
   * A do-all function that will apply a Seq of argument values to a Query and
   * for each set of arguments run an extractor function.
   *
   * @param rps The prepared statement. It is also expected to use a String as it
   *            can be implicitly converted to a prepared statement.
   * @param args A Seq of tuples containing the query arguments
   * @param f The extractor function
   * @tparam X The extracted row type
   * @return
   */
  def executeForResult[X](rps: RichPreparedStatement, args: Seq[Product])(f: RichResultSet => X): Seq[Vector[X]] = {
    val r = args.map { p =>
      p.productIterator.zipWithIndex.foreach {
        case (a: Boolean, i: Int) => rps.ps.setBoolean(i + 1, a)
        case (a: Byte, i: Int) => rps.ps.setByte(i + 1, a)
        case (a: Int, i: Int) => rps.ps.setInt(i + 1, a)
        case (a: Long, i: Int) => rps.ps.setLong(i + 1, a)
        case (a: Float, i: Int) => rps.ps.setFloat(i + 1, a)
        case (a: Double, i: Int) => rps.ps.setDouble(i + 1, a)
        case (a: String, i: Int) => rps.ps.setString(i + 1, a)
        case (a: Date, i: Int) => rps.ps.setDate(i + 1, a)
        case (a: Timestamp, i: Int) => rps.ps.setTimestamp(i + 1, a)
        case (Some(a), i: Int) => rps.ps.setObject(i + 1, a)
        case (None, i: Int) => rps.ps.setObject(i + 1, null)
        case (a: AnyRef, i: Int) => rps.ps.setObject(i + 1, a)
      }

      rps.execute(f).toVector
    }
    rps.close()
    r
  }

  /**
   * Run a simple query without arguments, then use the provided code block to
   * extract the values.
   *
   * @param rps The prepared statement.
   * @param f The extractor function.
   * @tparam X Extracted type.
   * @return
   */
  def executeForResult[X](rps: RichPreparedStatement)(f: RichResultSet => X): Vector[X] = {
    val r = rps.execute(f).toVector
    rps.close()
    r
  }

  /**
   * Just execute the given query. An optional Seq of tuples is accepted as
   * arguments for the query. No results are returned.
   *
   * The prepared statement will be closed after it is executed!
   *
   * @param rps The PreparedStatement
   * @param args The query arguments
   * @tparam X The type of extracted rows
   */
  def executeSimple[X](rps: RichPreparedStatement, args: Seq[Product] = Seq()) {

    if (args.length == 0) {
      rps.execute()
    }
    else {
      args.foreach { p =>
        p.productIterator.zipWithIndex.foreach {
          case (a: Boolean, i: Int) => rps.ps.setBoolean(i + 1, a)
          case (a: Byte, i: Int) => rps.ps.setByte(i + 1, a)
          case (a: Int, i: Int) => rps.ps.setInt(i + 1, a)
          case (a: Long, i: Int) => rps.ps.setLong(i + 1, a)
          case (a: Float, i: Int) => rps.ps.setFloat(i + 1, a)
          case (a: Double, i: Int) => rps.ps.setDouble(i + 1, a)
          case (a: String, i: Int) => rps.ps.setString(i + 1, a)
          case (a: Date, i: Int) => rps.ps.setDate(i + 1, a)
          case (a: Timestamp, i: Int) => rps.ps.setTimestamp(i + 1, a)
          case (Some(a), i: Int) => rps.ps.setObject(i + 1, a)
          case (None, i: Int) => rps.ps.setObject(i + 1, null)
          case (a: AnyRef, i: Int) => rps.ps.setObject(i + 1, a)
        }

        rps.execute()
      }
    }

    rps.close()
  }

  /**
   * Create a transaction around the queries run in the code block. If no
   * exceptions are thrown the transaction is committed, otherwise it is rolled
   * back.
   *
   * Beware: the transaction isolation will be set to SERIALIZABLE. Be prepared
   * to retry the whole block if you get the SQL serialization error. This means
   * that any side effects executed in the code block will be repeated so it is
   * best if the code block is side-effect free (aside from communicating with
   * the database)
   *
   * Beware(2): this was tested only on PostgreSQL and H2. YMMV
   *
   * @param f The code block that will run the queries.
   * @param conn Implicit database connection.
   * @tparam X The result type.
   * @return Either a Right with the result or a Left with the stack trace.
   */
  def transaction[X](f: => X)(implicit conn: Connection): Either[Throwable,X] = {
    val oldIsolationLevel = conn.getTransactionIsolation
    val oldAutoCommit     = conn.getAutoCommit

    conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE)
    conn.setAutoCommit(false)

    try {
      val r = f
      conn.commit()
      Right(r)
    }
    catch {
      case e: Exception =>
        conn.rollback()
        Left(e)
    }
    finally {
      conn.setTransactionIsolation(oldIsolationLevel)
      conn.setAutoCommit(oldAutoCommit)
    }
  }

  /**
   * A counterpart to RichResultSet, this class wraps the sql PreparedStatement
   * and adds a mini symbolic DSL for setting the query parameters.
   *
   * @param ps The wrapped PreparedStatement.
   */
  class RichPreparedStatement(val ps: PreparedStatement) {
    var pos = 1
    private def inc = { pos += 1; this }

    def execute[X](f: RichResultSet => X): Iterator[X] = {
      pos = 1

      new RSIter(ps.executeQuery).map(row => f(new RichResultSet(row)))
    }

    def <<![X](f: RichResultSet => X): Iterator[X] = execute(f)

    def execute = { pos = 1; ps.execute; this}
    def <<! = execute

    def close() { ps.close() }

    def <<(x: Boolean)      = { ps.setBoolean(pos, x);      inc }
    def <<(x: Byte)         = { ps.setByte(pos, x);         inc }
    def <<(x: Int)          = { ps.setInt(pos, x);          inc }
    def <<(x: Long)         = { ps.setLong(pos, x);         inc }
    def <<(x: Float)        = { ps.setFloat(pos, x);        inc }
    def <<(x: Double)       = { ps.setDouble(pos, x);       inc }
    def <<(x: String)       = { ps.setString(pos, x);       inc }
    def <<(x: Date)         = { ps.setDate(pos, x);         inc }
    def <<(x: Time)         = { ps.setTime(pos, x);         inc }
    def <<(x: Timestamp)    = { ps.setTimestamp(pos, x);    inc }
    def <<(x: InputStream)  = { ps.setBinaryStream(pos, x); inc }
    def <<(x: AnyRef)       = { ps.setObject(pos, x);       inc }
    def <<(x: Option[AnyRef]) = {
      x match {
        case Some(v) => ps.setObject(pos, v)
        case None    => ps.setObject(pos,null)
      }
      inc
    }
  }
}
