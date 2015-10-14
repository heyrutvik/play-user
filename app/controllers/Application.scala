package controllers

import play.api._
import play.api.mvc._

class Application extends Controller {

  // TODO 
  def index = Action {
    Ok(views.html.user.login())
  }

}
