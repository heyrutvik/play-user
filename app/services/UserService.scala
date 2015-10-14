package services

import play.api.cache._
import javax.inject.Inject
import models.User

case class UserService (cache: CacheApi, key: String) {
  def getUser() = cache.getOrElse[User](key)(User.findById(key.toInt))
}