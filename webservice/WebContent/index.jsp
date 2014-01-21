<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>

<t:application>
  <div class="row">
    <div class="col-md-12">
      <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do
        eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad
        minim veniam, quis nostrud exercitation ullamco laboris nisi ut
        aliquip ex ea commodo consequat. Duis aute irure dolor in
        reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla
        pariatur. Excepteur sint occaecat cupidatat non proident, sunt in
        culpa qui officia deserunt mollit anim id est laborum.</p>
    </div>
  </div>
  <!-- Form -->
  <div class="row">
    <div class="col-md-8">
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
      <form class="form-horizontal" role="form" action="solver" method="get">
        <div id="clue-input">
          <div class="form-group">
            <label for="clue" class="col-sm-3 control-label">Cryptic
              Clue</label>
            <div class="col-sm-7">
              <input type="text" class="form-control" id="clue" name="clue"
                value="${clue}"> <span class="help-block">Punctuation
                can also be included.</span>
            </div>
          </div>
          <div class="form-group">
            <label for="length" class="col-sm-3 control-label">Solution
              Length</label>
            <div class="col-sm-7">
              <input type="text" class="form-control" id="length" name="length"
                value="${length}"> <span class="help-block">Any
                combination of single words (e.g. 3), multiple words (e.g. 3,5)
                or hyphenated words (e.g. 3-5) can be entered.</span>
            </div>
          </div>
        </div>
        <div id="clue-pattern" class="form-group">
          <label for="pattern" class="col-sm-3 control-label">Solution
            Pattern</label>
          <div class="col-sm-7">
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
  <c:if test="${results != null}">
    <x:parse var="doc" doc="${results}"/>
    <hr>
    <!-- Results -->
    <div class="row">
      <div class="col-md-8">
        <h3>Results</h3>
        <p><b>Clue Recieved:</b> <x:out select="$doc/solver/clue"/></p>
        <p><b>Pattern Recieved:</b> <x:out select="$doc/solver/pattern"/></p>
        <x:choose>
          <x:when select="$doc/solver//solution">
            <ul class="list-group">
              <x:forEach select="$doc/solver/solution" var="solution">
                <li class="list-group-item">
                  <span class="badge"><x:out select="$solution/confidence"/> &#37;</span>
                  <x:out select="$solution/value"/>
                </li>
              </x:forEach>
            </ul>
          </x:when>
          <x:otherwise>
            <div class="alert alert-info">
              <b>Heads up!</b> The solvers have been unable to find a solution
              to your clue. Try widening your solution pattern by using more 
              unknown characters (?).
            </div>
          </x:otherwise>
        </x:choose>
      </div>
    </div>
  </c:if>
</t:application>
