package mystuff;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class Welcome {

	@GET
	public String hello() {
		return ("This is / yippie! <a href='/restbook3/customers'>Look at /customers</a>");
	}
}
