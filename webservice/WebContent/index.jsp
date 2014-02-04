<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:application>
  <div class="row">
    <div class="col-md-12">
      <div class="jumbotron">
        <h1>Cryptic Crossword Solver</h1>
        <p class="lead">Rude type making love twice on grass, perversely (5)</p>
      </div>
    </div>
  </div>
  <div class="row">
    <!-- Crossword Solver -->
    <div class="col-lg-4">
      <h2>Crossword Solver</h2>
      <p class="text-justify">The cryptic crossword solver found within this 
      site provides a number of integrated solving algorithms designed to 
      effortlessly solve the majority of clues. In order to solve a clue the 
      tool will only require the clue and the solution pattern. For access to 
      the tool, please click the button below.</p>
      <p>
        <a class="btn btn-primary" href="solver.jsp" role="button">
          Solve a clue
        </a>
      </p>
    </div>
    <!-- Help & Documentation -->
    <div class="col-lg-4">
      <h2>Help &amp; Documentation</h2>
      <p class="text-justify">Using the cryptic crossword solver's web interface
      couldn't be easier, however we have a comprehensive user guide that helps
      those who need it. There is also information for those who are wanting to 
      access our public restful interface to integrate in their own projects. 
      For more information on how to use our web interface or how to use the 
      public APIs, click the button below.</p>
      <p>
        <a class="btn btn-primary" href="help.jsp" role="button">
          View the manual
        </a>
      </p>
    </div>
    <!-- Changelog -->
    <div class="col-lg-4">
      <h2>Software Changelog</h2>
      <p class="text-justify">The project has been developed using an iterative
      process, and new features are constantly being added. For more information
      upon the changes please review the changelog, by clicking upon the button 
      below.</p>
      <p>
        <a class="btn btn-primary" href="changelog.jsp" role="button">
          View the changelog
        </a>
      </p>
    </div>
  </div>
</t:application>
