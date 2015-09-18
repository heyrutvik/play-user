package controllers

import play.api._
import play.api.mvc._
import models.{User => ModelUser}
import models.{Todo => ModelTodo}
import play.api.cache._
import javax.inject.Inject

import play.api.data._
import play.api.data.Forms._

class Todo @Inject() (cache: CacheApi) extends Controller {

  val todoForm = Form(
    mapping(
      "id" -> default(number, 0),
      "user_id" -> number,
      "title" -> nonEmptyText,
      "desc" -> nonEmptyText
    )(ModelTodo.apply)(ModelTodo.unapply)
  )

  def list = Action { implicit request =>
    request.session.get("session_user").map { user_id_string =>
      val user: ModelUser = cache.getOrElse[ModelUser](user_id_string) {
        println("AfterLogin")
        ModelUser.findById(user_id_string.toInt)
      }
      val todos = ModelTodo.listOfUser(user.id)
      // this is temporary, form will always blank with only logged in user id
      todoForm.fill(ModelTodo(user.id))
      Ok(views.html.todo.list(user, todos, todoForm))
    }.getOrElse {
      Unauthorized("Oops, you are not logged in :(")
    }
  }

  def add = Action { implicit request =>
    request.session.get("session_user").map { user_id_string =>
      val user: ModelUser = cache.getOrElse[ModelUser](user_id_string) {
        println("AfterLogin")
        ModelUser.findById(user_id_string.toInt)
      }
      val todos = ModelTodo.listOfUser(user.id)
      todoForm.bindFromRequest.fold(
        errors => BadRequest(views.html.todo.list(user, todos, errors)),
        todo => {
          println(todo)
          ModelTodo.save(todo)
          Redirect(routes.Todo.list)
        }
      )
    }.getOrElse {
      Unauthorized("Oops, you are not logged in :(")
    }
  }

  def delete(id: Int) = Action { implicit request =>
    request.session.get("session_user").map { user_id_string =>
      ModelTodo.delete(id, user_id_string.toInt)
      Redirect(routes.Todo.list)
    }.getOrElse {
      Unauthorized("Oops, you are not logged in :(")
    }
  }

}
