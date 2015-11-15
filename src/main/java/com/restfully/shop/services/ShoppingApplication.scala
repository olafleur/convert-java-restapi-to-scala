package com.restfully.shop.services

import java.util
import javax.ws.rs.core.Application

class ShoppingApplication extends Application {
   var singletons:java.util.Set[AnyRef] = new util.HashSet[AnyRef]()
   val empty = new util.HashSet[Class[_]]()

   singletons.add(new CustomerResource)

   override def getSingletons: util.Set[AnyRef] = singletons

   override def getClasses: util.Set[Class[_]] = empty
}
