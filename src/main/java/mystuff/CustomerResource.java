package mystuff;

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

   @GET
   public String hello() {
	   System.out.println("GET /customers");
	   return ("yay! you found me! I should code showing customers here." + customerDB);
   }

   @GET
   @Path("{id}")
   @Produces("application/xml")
   public String getCustomer(@PathParam("id") int id) {
	  System.out.println("GET /customers/" + id);
      final Customer customer = customerDB.get(id);
      if (customer == null) {
         throw new WebApplicationException(Response.Status.NOT_FOUND);
      }
      return "<customer id=\"" + customer.id + "\">" +
             "   <last-name>" + customer.lastName + "</last-name>" +
             "</customer>";
   }

   @POST
   @Consumes("application/xml")
   public Response createCustomer(String inputString) {
	  System.out.println("POST /customers:" + inputString);
      Customer customer = xmlToCustomer(inputString);
      customer.id = idCounter.incrementAndGet();
      customerDB.put(customer.id, customer);
      System.out.println("    Created customer " + customer.id);
      return Response.created(URI.create("/customers/" + customer.id)).build();

   }

   @PUT
   @Path("{id}")
   @Consumes("application/xml")
   public void updateCustomer(@PathParam("id") int id, String inputString) {
	  System.out.println("PUT /customers/" + id + ":" + inputString);
      Customer update = xmlToCustomer(inputString);
      Customer current = customerDB.get(id);
      if (current == null) throw new WebApplicationException(Response.Status.NOT_FOUND);

      current.lastName = update.lastName;
      System.out.println("   XML from request triggered update:" + current);
   }
   
   @DELETE
   public String deleteAllCustomers() {
	   customerDB.clear();
	   return "all customers deleted";
   }

   protected Customer xmlToCustomer(String xmlText) {
	      try {
	         DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	         InputStream is = new ByteArrayInputStream(xmlText.getBytes("UTF-8"));
	         Document doc = builder.parse(is);
	         Element root = doc.getDocumentElement();
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
