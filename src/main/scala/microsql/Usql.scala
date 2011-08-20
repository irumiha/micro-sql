package microsql;

import java.sql._
import java.io.InputStream

object Usql {
  case class TV(columnType: String, value: AnyRef)
  def rowToMap(rrs: RichResultSet): Map[String,TV] = {
    val md = rrs.rs.getMetaData
    val colCount = md.getColumnCount

    val nm = collection.mutable.Map[String,TV]()
    var cc = 1;
    while(cc <= colCount) {
      nm += (md.getColumnName(cc) -> TV(md.getColumnTypeName(cc), rrs.rs.getObject(cc)))
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

  private def strm[X](f: RichResultSet => X, rs: ResultSet): Iterator[X] = {
    if (!rs.next) Iterator.empty
    else new NIter[X](f(new RichResultSet(rs)),f,rs)
  }

  implicit def query[X](s: String, f: RichResultSet => X)(implicit stat: Statement) = {
      strm(f,stat.executeQuery(s));
  }

  implicit def conn2Statement(conn: Connection): Statement = conn.createStatement;

  implicit def rrs2Boolean(rs: RichResultSet)      = rs.nextBoolean;
  implicit def rrs2Byte(rs: RichResultSet)         = rs.nextByte;
  implicit def rrs2Int(rs: RichResultSet)          = rs.nextInt;
  implicit def rrs2Long(rs: RichResultSet)         = rs.nextLong;
  implicit def rrs2Float(rs: RichResultSet)        = rs.nextFloat;
  implicit def rrs2Double(rs: RichResultSet)       = rs.nextDouble;
  implicit def rrs2String(rs: RichResultSet)       = rs.nextString;
  implicit def rrs2Date(rs: RichResultSet)         = rs.nextDate;
  implicit def rrs2Time(rs: RichResultSet)         = rs.nextTime;
  implicit def rrs2Timestamp(rs: RichResultSet)    = rs.nextTimestamp;
  implicit def rrs2BinaryStream(rs: RichResultSet) = rs.nextBinStream;

  implicit def resultSet2Rich(rs: ResultSet) = new RichResultSet(rs);
  implicit def rich2ResultSet(r: RichResultSet) = r.rs;

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

    def foldLeft[X](init: X)(f: (ResultSet, X) => X): X = rs.next match {
      case false => init
      case true => foldLeft(f(rs, init))(f)
    }

    def map[X](f: ResultSet => X) = {
      var ret = List[X]()
      while (rs.next())
      ret = f(rs) :: ret
      ret.reverse; // ret should be in the same order as the ResultSet
    }
  }

  implicit def ps2Rich(ps: PreparedStatement) = new RichPreparedStatement(ps);
  implicit def rich2PS(r: RichPreparedStatement) = r.ps;

  def using(conn: Connection)(f: Connection => Unit) {
    f
  }

  implicit def str2RichPrepared(s: String)(implicit conn: Connection): RichPreparedStatement =
    conn prepareStatement(s);

  def withPrepared[X](rps: RichPreparedStatement)(f: RichPreparedStatement => X): X = {
    val r = f(rps)
    rps.close()
    r
  }

  def executeForResult[X](rps: RichPreparedStatement, args: Seq[Product])(f: RichResultSet => X): Seq[List[X]] = {

    val r = args.map{p =>
      p.productIterator.zipWithIndex.foreach(x => x match {
        case (a: Boolean,i: Int) => rps.ps.setBoolean(i + 1, a)
        case (a: Byte,i: Int) => rps.ps.setByte(i + 1, a)
        case (a: Int,i: Int) => rps.ps.setInt(i + 1, a)
        case (a: Long,i: Int) => rps.ps.setLong(i + 1, a)
        case (a: Float,i: Int) => rps.ps.setFloat(i + 1, a)
        case (a: Double,i: Int) => rps.ps.setDouble(i + 1, a)
        case (a: String,i: Int) => rps.ps.setString(i + 1, a)
        case (a: Date,i: Int) => rps.ps.setDate(i + 1, a)
        case (a: Timestamp,i: Int) => rps.ps.setTimestamp(i + 1, a)
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

  def executeSimple[X](rps: RichPreparedStatement) {
    rps.execute
    rps.close()
  }

  def executeSimple[X](rps: RichPreparedStatement, args: Seq[Product]) {

    args.foreach{p =>
      p.productIterator.zipWithIndex.foreach(x => x match {
        case (a: Boolean,i: Int) => rps.ps.setBoolean(i + 1, a)
        case (a: Byte,i: Int) => rps.ps.setByte(i + 1, a)
        case (a: Int,i: Int) => rps.ps.setInt(i + 1, a)
        case (a: Long,i: Int) => rps.ps.setLong(i + 1, a)
        case (a: Float,i: Int) => rps.ps.setFloat(i + 1, a)
        case (a: Double,i: Int) => rps.ps.setDouble(i + 1, a)
        case (a: String,i: Int) => rps.ps.setString(i + 1, a)
        case (a: Date,i: Int) => rps.ps.setDate(i + 1, a)
        case (a: Timestamp,i: Int) => rps.ps.setTimestamp(i + 1, a)
      })

      rps.execute
    }

    rps.close()
  }

  class RichPreparedStatement(val ps: PreparedStatement) {
    var pos = 1;
    private def inc = { pos = pos + 1; this }

    def execute[X](f: RichResultSet => X): Iterator[X] = {
      pos = 1; strm(f, ps.executeQuery)
    }

    def <<![X](f: RichResultSet => X): Iterator[X] = execute(f);

    def execute = { pos = 1; ps.execute; this}

    def <<! = execute;

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
  }
}
