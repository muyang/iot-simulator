/**
 *
 * @author Mu
 *
 * the app is for 
 * create hbase, named KafkaToHbase.
 */

import top.spoofer.hbrdd._
import top.spoofer.hbrdd.config.HbRddConfig
import top.spoofer.hbrdd.hbsupport.{FamilyPropertiesStringSetter, HbRddFamily}

object TableManager {
  private val tableName = "KafkaToHbase"

  private def getPropertiesFamilys = {
    /* modifying the column's maxversions*/
    val cf1 = HbRddFamily("SensorInfos", FamilyPropertiesStringSetter(Map("maxversions" -> "100")))
    Set(cf1)
  }

  def createTable(force: Boolean = false)(implicit hbRddConfig: HbRddConfig) = {
    val admin = HbRddAdmin.apply()

    if (force) {  //before create the table, delete the table with the same name
      admin.dropTable(tableName)
    }

    admin.createTableByProperties(tableName, this.getPropertiesFamilys)
    admin.close()
  }
}
