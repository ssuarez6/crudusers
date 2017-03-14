package co.s4n.users.persistance

import scala.collection.mutable.ListBuffer

case class User(id: Int, username: String) 

class UsersPersistance {
  var users = ListBuffer[User](User(1, "lasjkdf"), User(2, "lzxcv"), User(3, "qwer"), User(4, "rtyuur"))

  def add(username: String): Boolean = {
    if(users.exists(x => x.username == username)) false
    else{
      users.append(User(users.size+1, username))
      true
    }
  }

  def remove(id: Int): Boolean = {
    val opt: Option[User] = users.find(x => x.id == id)
    opt match {
      case Some(user: User) => {
        val indx = users.indexOf(user)
        users.remove(indx)
        true
      }
      case None => false
    }
  }

  def updateById(us: User): Boolean = {
    val opt: Option[User] = users.find(x => x.id == us.id)
    opt match {
      case Some(user) => {
        users.remove(users.indexWhere(x => x.id == user.id))
        users.append(us)
        true
      }
      case None => false
    }
  }
}
