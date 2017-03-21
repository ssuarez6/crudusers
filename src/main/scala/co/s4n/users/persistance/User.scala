package co.s4n.users.persistance

import java.util.UUID;
import scala.util.{Try, Success, Failure}

class EmptyFieldsException extends Exception("There is at least one field empty")
class NotAdultExecption extends Exception("Age needs to be 18 or more")

case class User(
  id: UUID, 
  username: String,
  fullName: String,
  age: Int
) 
/*
object User {
  override def apply(id: UUID, username: String, fullName: String, age: Int): Try[User] = {
    
  }
}*/
