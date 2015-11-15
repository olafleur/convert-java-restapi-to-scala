package com.restfully.shop.services

import java.util
import javax.ws.rs.core.Application

class ShoppingApplication extends Application {
   var singletons = new util.HashSet[AnyRef]()
   val empty = new util.HashSet[Class[_]]()

   singletons.add(new CustomerResource)

   override def getSingletons = singletons

   override def getClasses = empty
}
