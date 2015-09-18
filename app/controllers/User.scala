package controllers

import play.api._
import play.api.mvc._
import play.api.cache._
import javax.inject.Inject

import models.{User => ModelUser}

import play.api.data._
import play.api.data.Forms._

class User @Inject() (cache: CacheApi) extends Controller {

  val userForm = Form(
    mapping(
      "id" -> default(number, 0),
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
      "email" -> email
    )(ModelUser.apply)(ModelUser.unapply)
  )

  val loginForm = Form(
    tuple(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )
  )

  def loginPage = Action {
    Ok(views.html.user.login())
  }

  def loginUser = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      errors => BadRequest(views.html.user.login()),
      data => {
        // fetch use id if data: (String, String) is valid
        val user_id = ModelUser.authenticate(data)
        // redirect
        if (user_id > 0) {
          val user_id_string = user_id.toString
          val user: ModelUser = cache.getOrElse[ModelUser](user_id_string) {
            println("BeforeLogin")
            ModelUser.findById(user_id)
          }
          Redirect(routes.Todo.list).withSession("session_user" -> user_id_string)
        } else
          Redirect(routes.User.loginPage)
      }
    )
  }

  def logoutUser = Action {
    Redirect(routes.User.loginPage).withNewSession
  }

  def registerPage = Action {
    Ok(views.html.user.register(userForm))
  }

  def addUser = Action { implicit request =>
    userForm.bindFromRequest.fold(
      errors => BadRequest(views.html.user.register(errors)),
      user => {
        val id = ModelUser.save(user)
        Redirect(routes.User.loginPage)
      }
    )
  }

  def testUsers = Action {
    val users = ModelUser.allUser()
    Ok(views.html.user.test.users(users))
  }

}
