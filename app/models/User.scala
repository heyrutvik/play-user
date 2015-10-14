package models

import anorm._
import anorm.SqlParser._

import play.api.db._
import play.api.Play.current

//import play.api.libs._

case class User(
    id: Int = 0,
    username: String = "",
    password: String = "",
    email: String = "") {

  // imported to use companion object's method
  import User._

  def create: Int = {
    DB.withConnection { implicit c =>
      SQL(
        "INSERT INTO user (`username`, `password`, `email`) VALUES ({username}, {password}, {email})").on(
          "username" -> username,
          //"password" -> Crypto.encryptAES(password),
          "password" -> MD5(password),
          "email" -> email).executeInsert(scalar[Int].singleOpt).getOrElse(0)
      //).executeUpdate()
    }
  }

  def update: Int = {
    // should allow to call companion object method Or not because of case class?
    // import object, see above
    msg("Not Implemented Yet!", "update")
    id
  }

}

object User {

  val userRow = {
    get[Int]("id") ~
      get[String]("username") ~
      get[String]("password") ~
      get[String]("email") map {
        case id ~ username ~ password ~ email => User(id, username, password, email)
      }
  }

  def MD5(str: String) = {
    java.security.MessageDigest.getInstance("MD5").digest(str.getBytes)
  }

  def msg(str: String, any: Any) {
    println
    println(str + ": " + any)
    println
  }

  def authenticate(data: (String, String)): Int = {
    msg("user model authenticate", data)
    DB.withConnection { implicit c =>
      SQL(
        "SELECT * FROM user WHERE `username` = {username} AND `password` = {password} LIMIT 1").on(
          "username" -> data._1,
          "password" -> MD5(data._2)).as(SqlParser.int("id").singleOpt).getOrElse(0)
    }
  }

  def save(user: User): Int = {
    msg("user model save", user)
    if (user.id > 0)
      user.update
    else
      user.create
  }

  def allUser(): List[User] = {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM user").as(userRow *)
    }
  }

  def findById(user_id: Int): User = {
    DB.withConnection { implicit c =>
      SQL("SELECT * FROM user WHERE id = {id}").on("id" -> user_id).as(userRow.single)
    }
  }

}
