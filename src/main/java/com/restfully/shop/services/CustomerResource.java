package com.restfully.shop.services;

import com.restfully.shop.domain.Customer;
import org.w3c.dom.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.xml.parsers.*;
import java.io.*;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Path("/customers")
public class CustomerResource {
   private Map<Integer, Customer> customerDB = new ConcurrentHashMap<Integer, Customer>();
   private AtomicInteger idCounter = new AtomicInteger();

   public CustomerResource() {
   }
   
   @GET
   public String hello() {
	   System.out.println("GET /customers");
	   return ("yay! you found me! I should code showing customers here." + customerDB);
   }

   @POST
   @Consumes("application/xml")
   public Response createCustomer(String inputString) {
	  System.out.println("POST /customers:" + inputString);
      Customer customer = readCustomerUsingString(inputString);
      customer.id = idCounter.incrementAndGet();
      customerDB.put(customer.id, customer);
      System.out.println("Created customer " + customer.id);
      return Response.created(URI.create("/customers/" + customer.id)).build();

   }

   @GET
   @Path("{id}")
   @Produces("application/xml")
   public StreamingOutput getCustomer(@PathParam("id") int id) {
	  System.out.println("GET /customers/" + id);
      final Customer customer = customerDB.get(id);
      if (customer == null) {
         throw new WebApplicationException(Response.Status.NOT_FOUND);
      }
      return new StreamingOutput() {
         public void write(OutputStream outputStream) throws IOException, WebApplicationException {
            outputCustomer(outputStream, customer);
         }
      };
   }

   @PUT
   @Path("{id}")
   @Consumes("application/xml")
   public void updateCustomer(@PathParam("id") int id, String inputString) {
	  System.out.println("PUT /customers/" + id + ":" + inputString);
      Customer update = readCustomerUsingString(inputString);
      System.out.println("   customer read:" + update);
      Customer current = customerDB.get(id);
      if (current == null) throw new WebApplicationException(Response.Status.NOT_FOUND);

      current.lastName = update.lastName;
   }
   
   @DELETE
   public String deleteAllCustomers() {
	   customerDB.clear();
	   return "all customers deleted";
   }


   protected void outputCustomer(OutputStream os, Customer cust) throws IOException {
      PrintStream writer = new PrintStream(os);
      writer.println("<customer id=\"" + cust.id + "\">");
      writer.println("   <last-name>" + cust.lastName + "</last-name>");
      writer.println("</customer>");
   }

   protected Customer readCustomerUsingString(String inputString) {
	      try {
	         DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	         InputStream is = new ByteArrayInputStream(inputString.getBytes("UTF-8"));
	         Document doc = builder.parse(is);
	         System.out.println("doc:" + doc);
	         Element root = doc.getDocumentElement();
	         System.out.println("root=" + root);
	         Customer cust = new Customer();
	         if (root.getAttribute("id") != null && !root.getAttribute("id").trim().equals(""))
	            cust.id = Integer.valueOf(root.getAttribute("id"));
	         NodeList nodes = root.getChildNodes();
	         for (int i = 0; i < nodes.getLength(); i++) {
	            Element element = (Element) nodes.item(i);
	            if (element.getTagName().equals("last-name")) {
	               cust.lastName = element.getTextContent();
	            }
	         }
	         return cust;
	      }
	      catch (Exception e) {
	    	  System.out.println("cot:" + e);
	         throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
	      }
	   }
}
