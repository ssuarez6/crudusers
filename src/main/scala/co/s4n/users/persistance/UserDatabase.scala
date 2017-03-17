package co.s4n.users.persistance

import co.s4n.infrastructure.Connector._
import com.outworkers.phantom.dsl._

class UserDatabase(override val connector: KeySpaceDef) extends Database[UserDatabase](connector){
  object userModel extends ConcreteUserModel with connector.Connector
} 

object ProductionDb extends UserDatabase(connector)

trait ProductionDatabaseProvider {
  def database: UserDatabase
}

trait ProductionDatabase extends ProductionDatabaseProvider {
  override val database = ProductionDb
}

object EmbeddedDb extends UserDatabase(testConnector)

trait EmbeddedDatabaseProvider {
  def database: UserDatabase
}

trait EmbeddedDatabase extends EmbeddedDatabaseProvider {
  override val database = EmbeddedDb
}
