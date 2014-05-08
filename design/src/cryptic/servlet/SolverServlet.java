package cryptic.servlet;
import java.io.IOException;
import cryptic.solver.*;
@WebServlet("/solve")
public class SolverServlet extends Servlet {
    // Default Serial ID
    private static final long serialVersionUID = -7066687691201583586L;


    /**
     * Default Constructor
     */
    public SolverServlet() {
        super();
    }
     
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

    }
    
}
