package microsql
import scala.language.implicitConversions

import java.sql._
import java.io.InputStream
import scala.collection.Iterator

object SQL {
  val returnGeneratedKeys = true

  def extractToMap(rrs: RichResultSet): Map[String,Map[String,AnyRef]] = {
    val md = rrs.rs.getMetaData
    val colCount = md.getColumnCount

    val nm = collection.mutable.Map[String,Map[String,AnyRef]]()
    var cc = 1
    while(cc <= colCount) {
      val v = rrs.rs.getObject(cc)
      nm += (md.getColumnName(cc) -> Map(
        "type"  -> md.getColumnTypeName(cc),
        "value" -> Option(v)))
      cc += 1
    }

    nm.toMap
  }

  class NIter[X](var nvalue: X, val f: RichResultSet => X, val rs: ResultSet) extends Iterator[X] {
    var nextRowAvailable =  true
    override def hasNext = nextRowAvailable

    override def next() = {
      val r = nvalue
      if (rs.next) nvalue = f(new RichResultSet(rs))
      else nextRowAvailable = false

      r
    }
  }

  class RSIter(val rs: ResultSet) extends Iterator[ResultSet] {
    var nextAvailable = true
    var nextIsRead = false
    println(rs)

    override def hasNext = {
      if (nextIsRead) {
        nextAvailable = rs.next
        nextIsRead = false
      }
      if (!nextAvailable){
        val s = rs.getStatement
        rs.close()
        s.close()
      }
      nextAvailable
    }
    override def next() = {
      nextIsRead = true
      rs
    }
  }

  implicit def rpsToIter(rps: RichPreparedStatement): Iterator[ResultSet] = {
    val rs = rps.ps.getResultSet
    if (rs.next) new RSIter(rs)
    else Iterator.empty
  }

  private def strm[X](f: RichResultSet => X, rs: ResultSet): Iterator[X] = {
    if (!rs.next) Iterator.empty
    else new NIter[X](f(new RichResultSet(rs)),f,rs)
  }

  implicit def query[X](s: String, f: RichResultSet => X)(implicit stat: Statement): Iterator[X] = {
    strm(f,stat.executeQuery(s))
  }

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

    def foldLeft[X](init: X)(f: (ResultSet, X) => X): X = rs.next match {
      case false => init
      case true => foldLeft[X](f(rs, init))( f )
    }

    def map[X](f: ResultSet => X) = {
      var ret = List[X]()
      while (rs.next())
        ret = f(rs) :: ret
      ret.reverse // ret should be in the same order as the ResultSet
    }
  }

  implicit def ps2Rich(ps: PreparedStatement): RichPreparedStatement = new RichPreparedStatement(ps)
  implicit def rich2PS(r: RichPreparedStatement): PreparedStatement  = r.ps

  implicit def str2RichPrepared(s: String)(implicit conn: Connection): RichPreparedStatement =
    conn prepareStatement(s)

  def withPrepared[X](query: String, returnID: Boolean = false)(f: RichPreparedStatement => X)(implicit conn: Connection): X = {
    val rps: RichPreparedStatement =
      if (returnID)
        conn prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
      else
        conn prepareStatement(query)

    val r = f(rps)
    rps.close()
    r
  }

  def executeForResult[X](rps: RichPreparedStatement, args: Seq[Product])
                         (f: RichResultSet => X): Seq[List[X]] = {
    val r = args.map{p =>
      p.productIterator.zipWithIndex.foreach(x => x match {
        case (a: Boolean,i: Int)   => rps.ps.setBoolean(i + 1, a)
        case (a: Byte,i: Int)      => rps.ps.setByte(i + 1, a)
        case (a: Int,i: Int)       => rps.ps.setInt(i + 1, a)
        case (a: Long,i: Int)      => rps.ps.setLong(i + 1, a)
        case (a: Float,i: Int)     => rps.ps.setFloat(i + 1, a)
        case (a: Double,i: Int)    => rps.ps.setDouble(i + 1, a)
        case (a: String,i: Int)    => rps.ps.setString(i + 1, a)
        case (a: Date,i: Int)      => rps.ps.setDate(i + 1, a)
        case (a: Timestamp,i: Int) => rps.ps.setTimestamp(i + 1, a)
        case (Some(a), i: Int)     => rps.ps.setObject(i + 1, a)
        case (None, i: Int)        => rps.ps.setObject(i + 1, null)
        case (a: AnyRef,i: Int)    => rps.ps.setObject(i + 1, a)
      })

      rps.execute(f).toList
    }
    rps.close()
    r
  }

  def executeForResult[X](rps: RichPreparedStatement)(f: RichResultSet => X): List[X] = {
    val r = rps.execute(f).toList
    rps.close()
    r
  }

  def executeSimple[X](query: String)(implicit conn: Connection) {
    val rps: RichPreparedStatement = conn prepareStatement(query)
    rps.execute
    rps.close()
  }

  def executeSimple[X](rps: RichPreparedStatement, args: Seq[Product] = Seq()) {

    args.foreach{p =>
      p.productIterator.zipWithIndex.foreach(x => x match {
        case (a: Boolean,i: Int)   => rps.ps.setBoolean(i + 1, a)
        case (a: Byte,i: Int)      => rps.ps.setByte(i + 1, a)
        case (a: Int,i: Int)       => rps.ps.setInt(i + 1, a)
        case (a: Long,i: Int)      => rps.ps.setLong(i + 1, a)
        case (a: Float,i: Int)     => rps.ps.setFloat(i + 1, a)
        case (a: Double,i: Int)    => rps.ps.setDouble(i + 1, a)
        case (a: String,i: Int)    => rps.ps.setString(i + 1, a)
        case (a: Date,i: Int)      => rps.ps.setDate(i + 1, a)
        case (a: Timestamp,i: Int) => rps.ps.setTimestamp(i + 1, a)
        case (Some(a), i: Int)     => rps.ps.setObject(i + 1, a)
        case (None, i: Int)        => rps.ps.setObject(i + 1, null)
        case (a: AnyRef,i: Int)    => rps.ps.setObject(i + 1, a)
      })

      rps.execute
    }

    rps.close()
  }

  def transaction[X](f: => X)(implicit conn: Connection): Either[String,X] = {
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
      case e:Exception => {
        conn.rollback()
        Left(e.getMessage + "\n" + e.getStackTraceString)
      }
    }
    finally {
      conn.setTransactionIsolation(oldIsolationLevel)
      conn.setAutoCommit(oldAutoCommit)
    }
  }

  class RichPreparedStatement(val ps: PreparedStatement) {
    var pos = 1
    private def inc = { pos = pos + 1; this }

    def execute[X](f: RichResultSet => X): Iterator[X] = {
      pos = 1; strm(f, ps.executeQuery)
    }
    def <<![X](f: RichResultSet => X): Iterator[X] = execute(f)
    def execute = { pos = 1; ps.execute; this}
    def <<! = execute

    def close() { ps.close() }

    def <<(b: Boolean)      = { ps.setBoolean(pos, b);      inc }
    def <<(x: Byte)         = { ps.setByte(pos, x);         inc }
    def <<(i: Int)          = { ps.setInt(pos, i);          inc }
    def <<(x: Long)         = { ps.setLong(pos, x);         inc }
    def <<(f: Float)        = { ps.setFloat(pos, f);        inc }
    def <<(d: Double)       = { ps.setDouble(pos, d);       inc }
    def <<(o: String)       = { ps.setString(pos, o);       inc }
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
