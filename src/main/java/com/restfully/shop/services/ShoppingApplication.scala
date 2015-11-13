package com.restfully.shop.services

import javax.ws.rs.core.Application

import scala.collection.immutable.HashSet

class ShoppingApplication extends Application {
   var singletons = HashSet[Object]()
   val empty = HashSet[Class]()

   singletons = singletons + new CustomerResource
}
