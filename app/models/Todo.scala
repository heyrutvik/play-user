package models

import anorm._
import anorm.SqlParser._

import play.api.db._
import play.api.Play.current

case class Todo (
  id: Int = 0,
  user_id: Int = 0,
  title: String = "",
  desc: String = ""
) {

  def update: Int = {
    1
  }

  def create: Int = {
    DB.withConnection { implicit c =>
      SQL("INSERT INTO todo (user_id, title, desc) VALUES ({user_id}, {title}, {desc})").on("user_id" -> user_id, "title" -> title, "desc" -> desc).executeUpdate()
    }
  }

}

object Todo {

  val todoRow = {
    get[Int]("id") ~
      get[Int]("user_id") ~
      get[String]("title") ~
      get[String]("desc") map {
        case id ~ user_id ~ title ~ desc => Todo(id, user_id, title, desc)
      }
  }

  def apply(uid: Int): Todo = {
    Todo(user_id = uid)
  }

  def listOfUser(user_id: Int): List[Todo] = {
    DB.withConnection { implicit c =>
      SQL("SELECT * from todo where user_id = {user_id}").on("user_id" -> user_id).as(todoRow *)
    }
  }

  def delete(id: Int, user_id: Int) {
    DB.withConnection { implicit c =>
      SQL("DELETE FROM todo WHERE id = {id} AND user_id = {user_id}").on("id" -> id, "user_id" -> user_id).executeUpdate()
    }
  }

  def save(todo: Todo): Int = {
    if (todo.id > 0)
      todo.update
    else
      todo.create
  }
}
