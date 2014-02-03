<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>

<t:application>
  <div class="row">
    <div class="col-md-12">
      <p>Welcome to the Cryptic Crossword Solver. This web service has been 
        designed to solve the various types of cryptic crossword clues by 
        utilising various algorithms, dictionaries and thesauri.</p>
      <br>     
      <p>Please input a cryptic crossword clue, along with the expected answer 
        format and any known characters (optional).</p>
      <br>
    </div>
  </div>
  <!-- Form -->
  <div class="row">
    <div class="col-md-10 col-md-offset-1">
      <!-- Form Alerts -->
      <div id="form-alerts">
        <c:if test="${errors != null}">
          <div class="alert alert-danger">
            <b>Oh snap!</b> Change a few things up and try submitting again.
            <ol>
              <c:forEach var="message" items="${errors}">
                <li>${message}</li>
              </c:forEach>
            </ol>
          </div>
        </c:if>
      </div>
      <!-- Solver Form -->
      <form id="solver" class="form-horizontal" role="form" action="solver" method="post">
        <div id="clue-input">
          <div class="form-group">
            <label for="clue" class="col-sm-2 control-label">Cryptic
              Clue</label>
            <div class="col-sm-6">
              <input type="text" class="form-control" id="clue" name="clue"
                value="${clue}"> <span class="help-block">Punctuation
                can also be included.</span>
            </div>
          </div>
          <div class="form-group">
            <label for="length" class="col-sm-2 control-label">Solution
              Length</label>
            <div class="col-sm-6">
              <input type="text" class="form-control" id="length" name="length"
                value="${length}"> <span class="help-block">Any
                combination of single words (e.g. 3), multiple words (e.g. 3,5)
                or hyphenated words (e.g. 3-5) can be entered.</span>
            </div>
          </div>
        </div>
        <div id="clue-pattern" class="form-group">
          <label for="pattern" class="col-sm-2 control-label">Solution
            Pattern</label>
          <div class="col-sm-6">
            <input type="text" class="form-control" id="pattern"
              name="pattern" value="${pattern}"> <span
              class="help-block">Provide any known characters, unknown
              characters (?), word separators (comma) and hyphens (-).</span>
          </div>
        </div>
        <div id="clue-split-pattern" class="form-group"></div>
        <div class="form-actions">
          <input type="reset" class="btn btn-default" id="reset" value="Clear"/>
          <input type="submit" class="btn btn-primary" id="submit" value="Submit"/>
        </div>
      </form>
    </div>
  </div> 
  <div class="row">
    <!-- Results -->
    <div id="results" class="col-md-10  col-md-offset-1">
      <c:if test="${results != null}">
        <x:parse var="doc" doc="${results}"/>     
        <h3>Results</h3>
        <p><b>Clue Received:</b> <x:out select="$doc/solver/clue"/></p>
        <p><b>Pattern Received:</b> <x:out select="$doc/solver/pattern"/></p>
        <x:choose>
          <x:when select="$doc/solver//solution">
            <div class="panel-group" id="accordion">
              <x:forEach select="$doc/solver/solution" var="solution">
                <div class="panel panel-default">
                  <div class="panel-heading">
                    <h3 class="panel-title"><x:out select="$solution/value"/></h3>
                    <span class="label label-default pull-right">
                      <x:out select="$solution/confidence"/>&#37;
                    </span>
                    <span class="label label-info pull-right">
                      <x:out select="$solution/solver"/>
                    </span>
                  </div>
                  <div class="panel-body">
                    <x:choose>
                      <x:when select="$solution/trace">
                        <p>Solution Trace:</p>
                        <ol>
                          <x:forEach select="$solution/trace" var="trace">
                            <li><x:out select="$trace"/></li>
                          </x:forEach>
                        </ol>
                      </x:when>
                      <x:otherwise>
                        <p>Solution Trace Unavailable.</p>
                      </x:otherwise>
                    </x:choose>
                  </div>
                </div>
              </x:forEach>
            </div>
          </x:when>
          <x:otherwise>
            <div class="alert alert-info">
              <b>Heads up!</b> The solvers have been unable to find a solution
              to your clue. Try widening your solution pattern by using more 
              unknown characters (?).
            </div>
          </x:otherwise>
        </x:choose>
      </c:if>
    </div>
  </div>
  <div id="loading" class="modal fade">
    <div class="modal-dialog modal-vertical-centered">
      <div class="modal-content text-center">
        <div class="modal-header">
          <h4 class="modal-title">Solving clue, please wait...</h4>
        </div>
        <div class="modal-body">
          <img alt="loading" src="/cryptic/images/spinner.gif">
        </div>
      </div>
    </div>
  </div>
</t:application>