package com.restfully.shop.services

import java.io.{InputStream, OutputStream}
import java.net.URI
import java.util.concurrent.atomic.AtomicInteger
import javax.ws.rs.{Consumes, GET, POST, PUT, Path, PathParam, Produces, WebApplicationException}
import javax.ws.rs.core.{Response, StreamingOutput}
import javax.xml.parsers.DocumentBuilderFactory

import com.restfully.shop.domain.Customer
import org.w3c.dom.Element

import scala.collection.mutable

@Path("/customers")
class CustomerResource() {
  private val customerDB = mutable.Map[Integer, Customer]()
  private val idCounter = new AtomicInteger()

  @POST
  @Consumes(Array("application/xml"))
  def createCustomer(inputStream: InputStream) = {
    val customer = readCustomer(inputStream)
    customer.setId(idCounter.incrementAndGet())
    customerDB.put(customer.getId, customer)
    println("Created customer " + customer.getId)

    Response.created(URI.create("/customers/" + customer.getId)).build()

  }

  @GET
  @Path("{id}")
  @Produces(Array("application/xml"))
  def getCustomer(@PathParam("id") id: Integer) = {
    val customer = customerDB.get(id)

    customer match {
      case Some(cust) => new StreamingOutput() {
        def write(outputStream: OutputStream) {
          outputCustomer(cust)
        }
      }

      case None => throw new WebApplicationException(Response.Status.NOT_FOUND)
    }
  }

  @PUT
  @Path("{id}")
  @Consumes(Array("application/xml"))
  def updateCustomer(@PathParam("id") id: Integer, is: InputStream) = {
    val update = readCustomer(is)

    customerDB.get(id) match {
      case Some(current) =>
        current.setFirstName(update.getFirstName)
        current.setFirstName(update.getFirstName)
        current.setLastName(update.getLastName)
        current.setStreet(update.getStreet)
        current.setState(update.getState)
        current.setZip(update.getZip)
        current.setCountry(update.getCountry)
      case None => throw new WebApplicationException(Response.Status.NOT_FOUND)
    }
  }

  protected def outputCustomer(cust: Customer) {
    println("<customer id=\"" + cust.getId + "\">")
    println("   <first-name>" + cust.getFirstName + "</first-name>")
    println("   <last-name>" + cust.getLastName + "</last-name>")
    println("   <street>" + cust.getStreet + "</street>")
    println("   <city>" + cust.getCity + "</city>")
    println("   <state>" + cust.getState + "</state>")
    println("   <zip>" + cust.getZip + "</zip>")
    println("   <country>" + cust.getCountry + "</country>")
    println("</customer>")
  }

  protected def readCustomer(is: InputStream) = {
    val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val doc = builder.parse(is)
    val root = doc.getDocumentElement
    val cust = new Customer()
    val nodes = root.getChildNodes

    if (root.getAttribute("id") != null && !root.getAttribute("id").trim().equals(""))
      cust.setId(Integer.valueOf(root.getAttribute("id")))

    for (i <- 0 until nodes.getLength) {

      val element = nodes.item(i) match {
        case elem: Element => elem
        case _ => throw new ClassCastException
      }

      if (element.getTagName.equals("first-name")) {
        cust.setFirstName(element.getTextContent)
      }
      else if (element.getTagName.equals("last-name")) {
        cust.setLastName(element.getTextContent)
      }
      else if (element.getTagName.equals("street")) {
        cust.setStreet(element.getTextContent)
      }
      else if (element.getTagName.equals("city")) {
        cust.setCity(element.getTextContent)
      }
      else if (element.getTagName.equals("state")) {
        cust.setState(element.getTextContent)
      }
      else if (element.getTagName.equals("zip")) {
        cust.setZip(element.getTextContent)
      }
      else if (element.getTagName.equals("country")) {
        cust.setCountry(element.getTextContent)
      }
    }

    cust
  }
}
