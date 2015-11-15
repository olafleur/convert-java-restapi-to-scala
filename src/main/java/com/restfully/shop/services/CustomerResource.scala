package com.restfully.shop.services

import java.io.{InputStream, OutputStream, PrintStream}
import java.net.URI
import java.util.concurrent.atomic.AtomicInteger
import javax.ws.rs.{Consumes, GET, POST, PUT, Path, PathParam, Produces, WebApplicationException}
import javax.ws.rs.core.{Response, StreamingOutput}
import javax.xml.parsers.DocumentBuilderFactory

import com.restfully.shop.domain.Customer
import org.w3c.dom.{Element, NodeList}

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
    System.out.println("Created customer " + customer.getId)

    Response.created(URI.create("/customers/" + customer.getId)).build()

  }

  @GET
  @Path("{id}")
  @Produces(Array("application/xml"))
  def getCustomer(@PathParam("id") id: Integer) = {
    val customer = customerDB.get(id)
    if (customer == null) {
      throw new WebApplicationException(Response.Status.NOT_FOUND)
    }

    new StreamingOutput() {
      def write(outputStream: OutputStream) {
        outputCustomer(outputStream, customer.get)
      }
    }
  }

  @PUT
  @Path("{id}")
  @Consumes(Array("application/xml"))
  def updateCustomer(@PathParam("id") id: Integer, is: InputStream) = {
    val update: Customer = readCustomer(is)
    val current: Customer = customerDB.get(id).get
    if (current == null) throw new WebApplicationException(Response.Status.NOT_FOUND)

    current.setFirstName(update.getFirstName)
    current.setLastName(update.getLastName)
    current.setStreet(update.getStreet)
    current.setState(update.getState)
    current.setZip(update.getZip)
    current.setCountry(update.getCountry)
  }


  protected def outputCustomer(os: OutputStream, cust: Customer) {
    val writer = new PrintStream(os)
    writer.println("<customer id=\"" + cust.getId + "\">")
    writer.println("   <first-name>" + cust.getFirstName + "</first-name>")
    writer.println("   <last-name>" + cust.getLastName + "</last-name>")
    writer.println("   <street>" + cust.getStreet + "</street>")
    writer.println("   <city>" + cust.getCity + "</city>")
    writer.println("   <state>" + cust.getState + "</state>")
    writer.println("   <zip>" + cust.getZip + "</zip>")
    writer.println("   <country>" + cust.getCountry + "</country>")
    writer.println("</customer>")
  }

  protected def readCustomer(is: InputStream): Customer = {
    val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val doc = builder.parse(is)
    val root = doc.getDocumentElement
    val cust = new Customer()
    if (root.getAttribute("id") != null && !root.getAttribute("id").trim().equals(""))
      cust.setId(Integer.valueOf(root.getAttribute("id")))
    val nodes: NodeList = root.getChildNodes
    for (i <- 1 until nodes.getLength) {
      val element: Element = nodes.item(i) match {
        case g2: Element => g2
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
