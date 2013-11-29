package uk.ac.hud.cryptic.servlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.xml.ws.http.HTTPException;
/**
 * This class provides a servlet wrapper around the Hidden algorithm. Clues are
 * solved by posting the clue and the expected solution length to this resource.
 *
 * @author  Luke Hackett
 * @version 0.1
 */
@WebServlet("/solver/hidden")
public class HiddenServlet extends Servlet {
    // Default Serial ID
    private static final long serialVersionUID = -7066687691201583586L;


    /**
     * Default Constructor
     */
    public HiddenServlet() {
        super();
    }
     
    
    /**
     * This method will allow for two parameters to be posted -- clue and 
     * length. Clue refers to the original clue, and the solutionLength refers 
     * to the length of the solution. Responses are returned as either XML 
     * (default) or JSON depending upon the client's request.
     * 
     * @param  request   HTTP request information
     * @param  response  HTTP response information
     * 
     * @throws ServletException
     * @throws IOException
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Obtain the input requests
        String clue = request.getParameter("clue");
        String solutionLength = request.getParameter("solutionLength");
        
        // Ensure that the inputs have been sent
        if (clue == null || solutionLength == null) {
            // TODO log exceptions
            throw new HTTPException(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        // Format some test data
        String data = "";
        data += "<response>";
        data += "<clue_recieved>" + clue + "</clue_recieved>";
        data += "<sLen_recieved>" + solutionLength + "</sLen_recieved>";
        data += "</response>";
                
        // Entry point to Hidden Solver
        //new Thread(new Hidden()).start();
        
        
        // Send the response
        boolean json = isJSONRequest(request);
        sendResponse(response, data, json);
    }
    
}
