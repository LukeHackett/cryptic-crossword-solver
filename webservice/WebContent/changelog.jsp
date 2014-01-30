<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:application>
  <!-- Milestone 1 -->
  <div class="row">
    <div class="col-md-12">
    <h2>Cryptic Crossword Solver Changelog</h2>
    <hr>
    </div>
  </div>
  <!-- Milestone 1 -->
  <div class="row">
    <div class="col-md-12">
      <h3>Milestone 1 <small>29th January 2014</small></h3>
      <p>Today marks the completion of the first milestone. Although the solver
      only currently supports a basic set of solvers, there has been plenty of
      work completed so that additional solvers can be <i>hopefully</i> added in
      no time at all. 
	    <div class="row">
		    <div class="col-md-4">
		      <h4>Key Changes</h4>
		      <ul>
		        <li>Responsive HTML5 user interface, supporting desktops and mobile
		        devices with and without JavaScript.</li>
		        <li>Support for Acrostic, Anagram, Hidden and Pattern cryptic clues.
		        </li>
		        <li>Threaded solving support, enabling each solver to finish around
		        the same time.</li>
		        <li>Interfaces to official cryptic crossword dictionaries and 
		        thesauri (with support for custom additions and exclusions).</li>
		        <li>A framework skeleton for calculating confidence ratings.</li>
		        <li>A framework for solution traces which will highlight the "best"
		        path in order to solve the given clue.</li>
		        <li>A database of cryptic clues and solutions has been created,
		        with kind permission of <a href="http://www.theguardian.com/uk"
		        target="_blank">the Guardian</a>, and is used in testing
		        each of the solvers.</li>
		      </ul>
		    </div>
		    <div class="col-md-4">
		      <h4>Known Bugs</h4>
	        <ul>
	          <li>JavaScript UI does not allow numbers greater than 9 to be input.
	          </li>
	          <li>There are a few minor styling issues, but do not affect the 
	          functionality of the site.</li>
	          <li>Anagram solver is not efficient with some clues, and hence there
	          may be a (considerable) wait time.</li>
	        </ul>
		    </div>
		    <div class="col-md-4">
		      <h4>In Progress</h4>
		      <ul>
		        <li>Addition of new solvers including Palindromes, Double 
		        Definition, Shifting, Exchange and Spoonerisms.</li>
		        <li>UI logic and styling bug fixes.</li>
		      </ul>
		    </div>
		  </div>
		  <hr>
		</div>
  </div>
</t:application>