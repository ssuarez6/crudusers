package co.s4n.users.persistance

import java.util.UUID;

case class User(
  id: UUID, 
  username: String,
  fullName: String,
  age: Int
) {
  require(!username.isEmpty, "username must not be empty")
  require(!fullName.isEmpty, "your name must not be empty")
  require(age>=18, "you must be 18 or more to be registered in our system")
}
