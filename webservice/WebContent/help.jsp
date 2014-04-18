<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:application>
  <div class="row">
    <div class="col-md-12">
      <h2>User Manual</h2>
      <p>The usage of the site has been designed to be as simple and free 
      flowing as possible. However for total piece at mind there are two user 
      guides that may be of some help</p>
      <p>The majority of users will use the simple HTML interface that has been
      designed and implemented as part of this website. However there is also a 
      web service manual for those who are interested in utilising the RESTful 
      web service.</p>
      <ul>
        <li><a href="#website">View the website manual</a></li>
        <li><a href="#webservice">View the web service manual</a></li>
      </ul>
    </div>
  </div>
  <hr>
  <!-- Website Manual -->
  <div id="website" class="row">
    <div class="col-md-12">
      <h3>Website Manual</h3>
      <p>The main usage for many cryptic crossword solvers will be via the 
      dedicated website. The website has a <a href="solver.jsp">solving form</a>
      that is able to viewed from within <strong>any</strong> HTML standard web 
      browser upon <strong>any</strong> device.</p>
      <p>The website form has been designed so that as little input from the 
      user is required as possible.</p>
      <p>Upon inputting a correct clue and solution length pattern the text 
      boxes will turn green. If invalid data has been input, the text boxes will
      turn read.</p>
      <p>Once both the clue and the solution length have been input, the user
      is able to add additional known characters (if possible) within the 
      various supplied text boxes. These text boxes are automatically generated
      and can be left blank.</p>
      <p><strong>Note:</strong> blank text boxes will be automatically mapped to
      wild card characters, which may have serious performance issues when using
      many blank text boxes upon large solutions.</p>
      <p>An example completed form is shown below:</p>
      <br>
      <!-- Example Form -->
      <form class="form-horizontal" role="form">
        <!-- Clue Input -->
        <div class="form-group has-success">
          <label for="clue" class="col-sm-2 control-label">Cryptic Clue</label>
          <div class="col-sm-6">
            <input type="text" class="form-control" value="Introduction to do-gooder canine"> 
            <span class="help-block">
              Punctuation can also be included.
            </span>
          </div>
        </div>
        <!-- Clue Length Input -->
        <div class="form-group has-success">
          <label for="length" class="col-sm-2 control-label">Solution Length</label>
          <div class="col-sm-6">
            <input type="text" class="form-control" value="3"> 
            <span class="help-block">
              Any combination of single words (e.g. 3), multiple words (e.g. 3,5)
              or hyphenated words (e.g. 3-5) can be entered.
            </span>
          </div>
        </div>
        <!-- Character Input -->
        <div class="form-group">
          <label class="col-sm-2 control-label">Word 1</label>
          <div class="col-sm-10 word">
            <input type="text" class="form-control" maxlength="1" size="1" value="d">
            <input type="text" class="form-control" maxlength="1" size="1">
            <input type="text" class="form-control" maxlength="1" size="1">
          </div>
        </div>
        <!-- Form Actions -->
        <div class="form-actions">
          <input type="button" class="btn btn-default" value="Clear">
          <input type="button" class="btn btn-primary" value="Submit">
        </div>
      </form>
    </div>
  </div>
  <hr>
  <!-- Web Service Manual -->
  <div id="webservice" class="row">
    <!-- Instructions -->
    <div class="col-md-6">
      <h3>Web Service Manual</h3>
      <p>The cryptic crossword solver web site is powered via Apache Tomcat,
        which is serving a RESTful web service. It is possible for external 
        clients to be able to tap into the data processing algorithms by simply 
        sending three parameters to the service. These are:</p>
      <ul>
        <li><strong>clue</strong> - the clue to be solved</li>
        <li><strong>length</strong> - the length of the solution</li>
        <li><strong>pattern</strong> - the expected pattern of the solution</li>
      </ul>
      <p>The parameters can be given in any order, however all three parameters 
      must be given otherwise the response will be an error.</p>
      <br>
      <p>The clue can be of any length, and may contain spaces, however it is 
      recommended that the data send is encoded if the request is being made 
      via AJAX.</p>
      <p>The solution length is a number or any combination of numbers, commas 
      and hyphens to denoted a separate word or hyphened-word.</p>
      <p>The solution pattern must match exactly to the solution length, for 
      example a given clue length <kbd>4,3-3</kbd> would match to <kbd>????,???-???</kbd>
      or <kbd>?a??,???-??t</kbd> solution patterns but would not match to this
      <kbd>???????-???</kbd> solution pattern.</p>
      <br>
      <p>The web service supports both HTTP GET and HTTP POST requests, all
      other requests will be ignored as they have not been implemented.</p>
      <p>The server that hosts the web service also hosts the main website 
      that allows users to use the solver directly upon the web. In order to 
      inform the server that you are invoking it's service, you will need to 
      pass the <kbd>X-Requested-With</kbd> flag and set it to 
      <kbd>XMLHttpRequest</kbd>.</p>
      <p>The web service is also able to return JSON or XML data, but by default
      will return XML. To change this the <kbd>Accept</kbd> header will need to 
      be set to either <kbd>application/json</kbd> for a JSON data return or to 
      <kbd>application/xml</kbd> for an XML data return.</p>
      <br>
      <p>An example python script is shown of exactly how the service can be 
      invoked utilising the urllib libraries.</p>
    </div>
    <!-- Code Listing -->
    <div class="col-md-6">
      <pre id="python">
      import urllib
      import urllib2

      # URL of the web service
      url = 'http://crypticsolver.com/solver'

      # Get the user input
      clue = "Introduction to do-gooder canine"
      length = 3
      pattern = "???"

      # HTTP Headers
      header = { 
        'Accept'           : 'application/json',
        'X-Requested-With' : 'XMLHttpRequest'
      }

      # HTTP Post Data
      values = {
        'clue'    : clue,
        'length'  : length,
        'pattern' : pattern
      }
        
      # URL Encode the value
      data = urllib.urlencode(values)

      # Make a new POST request
      req = urllib2.Request(url, data, header)

      # obtain the response
      response = urllib2.urlopen(req)

      # print the response
      print response.read()
      </pre>
    </div>
  </div>
</t:application>