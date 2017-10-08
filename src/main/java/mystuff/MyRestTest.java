package mystuff;


import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;

public class MyRestTest {
    @BeforeClass
    public static void init() {
        RestAssured.baseURI = "http://localhost/restbook3";
        RestAssured.port = 8080;
        RestAssured.registerParser("text/plain", Parser.JSON);
    }

    
	//body("greeting.firstName", equalTo("John"));    	
	/*
	
	expect().body("title", 
			equalTo("Hello world!")).when().
	get("http://localhost:8080/restfulbook/timezone");
	*/

	/*
    get("/restfulbook/timezone")
    .then()
    .body("id", equalTo(12))
    .body("firstName", equalTo("Vinod"))
    .body("lastName", equalTo("Kashyap"))
    .body("designation", equalTo("CEO"));
    */
	private void getACustomer(String lastName) {
		// just created customer 1, now get it
    	given().
        when().
        get("/customers/1").
        then().
        statusCode(200).
        body("customer.last-name" , equalTo(lastName));
	}
    
    
	private void deleteAllCustomers() {
		System.out.println("deleteAll Customers start");
		// delete em all
    	when().
        request("DELETE", "/customers").
        then().
        statusCode(200);
		System.out.println("deleteAll Customers finish");
	}

    @Test
    public void testPostCustomers() {
    	deleteAllCustomers();
        createOneCustomer();
    	
    	getACustomer("Burke");
    	
    }


	private void createOneCustomer() {
    	deleteAllCustomers();    	

    	String newCustomer = "<customer>"
                + "<first-name>Bill</first-name>"
                + "<last-name>Burke</last-name>"
                + "<street>256 Clarendon Street</street>"
                + "<city>Boston</city>"
                + "<state>MA</state>"
                + "<zip>02115</zip>"
                + "<country>USA</country>"
                + "</customer>";
        
    	given().
    	contentType(ContentType.XML).
    	body(newCustomer).
        when().
        post("/customers").
        then().
        statusCode(201);
	}


    @Test
    public void testGetOtherPage() {
    	
    	when().
        request("GET", "/").
        then().
        statusCode(200);
    }
    	
    @Test
	public void testPutCustomer1() {
		String newCustomer = "<customer>"
                + "<first-name>Bill</first-name>"
                + "<last-name>Doe</last-name>"
                + "<street>256 Clarendon Street</street>"
                + "<city>Boston</city>"
                + "<state>MA</state>"
                + "<zip>02115</zip>"
                + "<country>USA</country>"
                + "</customer>";
    	given().
    	contentType(ContentType.XML).
    	body(newCustomer).
        when().
        put("/customers/1").
        then().
        statusCode(204);
    	
    	getACustomer("Doe");
	}

}
