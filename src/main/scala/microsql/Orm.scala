package microsql

import java.sql.{Connection, ResultSet}


object Orm {
  abstract class SQLConstraint
  case object IsNull extends SQLConstraint
  case object IsNotNull extends SQLConstraint

  trait LongIDKey {
    var id: Long = _
  }

  def entity[T](tname: String)(implicit man: Manifest[T]) = {
    val proto = man.erasure
    val attributes = proto.getDeclaredFields.map(f => (f.getName, f.getType.getName))

    (proto.getName, (tname, attributes))
  }

  trait Schema {
    val entities: Map[String,(String,Array[(String,String)])]

    def extract[T](rs: ResultSet)(implicit man: Manifest[T]): T = {
      val attributes = entities(man.erasure.getName)._2
      val constructor = man.erasure.getConstructors.head // We expect to have only one constructor
      val args = attributes map (a => rs.getObject(a._1))

      val r = constructor.newInstance(args.take(constructor.getParameterTypes.length): _*).asInstanceOf[T]

      // We search for the remaining argument not consumed by the constructor.
      // Take note: only one argument (Integer or Long) will be taken into account here
      // and assigned to the id field of the resulting case class
      args.drop(constructor.getParameterTypes.length).headOption.flatMap{ x => x match {
        case id: java.lang.Integer => {
          val r2 = r.asInstanceOf[LongIDKey]
          r2.id = id.longValue()
          Some(r2.asInstanceOf[T])
        }
        case id: java.lang.Long => {
          val r2 = r.asInstanceOf[LongIDKey]
          r2.id =id.longValue()
          Some(r2.asInstanceOf[T])
        }
      }}.orElse{Some(r)}.get
    }

    def insert[T](ent: T)(implicit conn: Connection, man: Manifest[T]): T = {
      import microsql.SQL._
      val entData = entities(man.erasure.getName)
      val isKeyed = man.erasure.getInterfaces.map(_.getName).find(_ == "microsql.Orm$LongIDKey").isDefined
      val (tableName,fields) = entData
      val fieldsForQuery = if (isKeyed) fields.filter(_._1 != "id") else fields

      val insertStr =
        ("insert into "+
          tableName+
          " ("+fieldsForQuery.map(_._1).mkString(",")+
          ") values ("+
          fieldsForQuery.map(x => "?").mkString(",")+")")

      withPrepared(insertStr,returnGeneratedKeys) { s =>
        ent.asInstanceOf[Product].productIterator.foldLeft(s){(s,f) => s << f.asInstanceOf[AnyRef]}
        val newKey = Option(s.execute.ps.getGeneratedKeys)
        if (isKeyed && newKey.isDefined && newKey.get.next) {
          val rs = newKey.get
          if (rs.getMetaData.getColumnCount == 1)
            loadByID[T](newKey.get.getLong(1)).get // most other databases only return the new ID so use that for lookup
          else
            extract[T](newKey.get) // PostgreSQL returns the whole inserted row so use it
        }
        else
          ent
      }
    }

    def update[T](ent: T)(implicit conn: Connection, man: Manifest[T]): T = {
      import microsql.SQL._
      val entData = entities(man.erasure.getName)
      val (tableName,fields) = entData
      val fieldsNoID = fields.filter(_._1 != "id")
      val insertStr =
        "update "+tableName+" set "+fieldsNoID.map(_._1 + "=?").mkString(",") + " where id=?"

      val s = ent.asInstanceOf[Product].productIterator.foldLeft(insertStr:RichPreparedStatement){(s,f) => s << f.asInstanceOf[AnyRef]}
      (s << ent.asInstanceOf[{var id: Long}].id <<!)
      ent
    }

    def loadByID[T](id: Long)(implicit conn: Connection, man: Manifest[T]): Option[T] = {
      import microsql.SQL._
      val entData = entities(man.erasure.getName)
      val (tableName,fields) = entData
      val fetchStr = "select * from "+tableName+" where id=?"

      (fetchStr << id execute { extract[T](_) }).toList.headOption
    }

    def deleteByID[T](id: Long)(implicit conn: Connection, man: Manifest[T]) {
      import microsql.SQL._
      val entData = entities(man.erasure.getName)
      val (tableName,fields) = entData
      val fetchStr = "delete * from "+tableName+" where id=?"

      (fetchStr << id <<!).execute
    }
  }
}