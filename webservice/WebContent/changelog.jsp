<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:application>
  <div class="row">
    <div class="col-md-12">
      <h2>Development Changelog</h2>
      <hr>
    </div>
  </div>
  <!-- Iteration 3 -->
  <div class="row">
    <div class="col-md-12">
      <h3>Iteration 3 <small>4th April 2014</small></h3>
      <p>Today marks the completion of the third and final iteration. Within 
        this iteration a number of new solvers have been brought on line, as 
        well as various improvements to the current range of solvers.</p>
      <div class="row">
      <!-- Key Changes -->
        <div class="col-md-4">
          <h4>Key Changes</h4>
          <ul>
            <li>Addition of a user manual, containing information for developers
            and users of the system.</li>
            <li>Added support for Container, Charade, Deletion and Reversal clue
            types.</li>
            <li>Improved the solution traces to aid new users in solving clues
            </li>
          </ul>
        </div>
        <!-- Known Bugs -->
        <div class="col-md-4">
          <h4>Known Bugs</h4>
          <ul>
            <li>None</li>
          </ul>
        </div>
        <!-- In Progress -->
        <div class="col-md-4">
          <h4>In Progress</h4>
          <ul>
            <li>None</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
  <hr>
  <!-- Iteration 2 -->
  <div class="row">
    <div class="col-md-12">
      <h3>Iteration 2 <small>4th March 2014</small></h3>
      <p>Today marks the completion of the second iteration. Within this 
        iteration a large amount of time has been spent adding additional 
        solvers, which will help in solving a wider range of clues. The user 
        interface improvements not only fix bugs, but add additional 
        functionality which will aid users in solving cryptic crosswords.</p>
      <div class="row">
        <!-- Key Changes -->
        <div class="col-md-4">
          <h4>Key Changes</h4>
          <ul>
            <li>User Interface has been completely re-written from the ground 
              up, fixing multiple bugs.
            </li>
            <li>All styling issues have now been resolved.</li>
            <li>The UI now handles results more effectively, including 
              displaying the type of solver that obtained the solution and how 
              the solution was obtained.</li>
            <li>Additional support has been added for Double Definition, 
              Homophone, Palindrome and Spoonerism cryptic clues.
            </li>
            <li>Confidence ratings have been improved across the board, allowing 
              for various factors to be taken in to consideration when 
              calculating the confidence of a solution.</li>
          </ul>
        </div>
        <!-- Known Bugs -->
        <div class="col-md-4">
          <h4>Known Bugs</h4>
          <ul>
            <li>The system does not have any understanding of words, and thus 
              may not be able to return solutions to some clues.</li>
            <li>The system does not have a time-out function, and thus clues 
              that may take some time to finish will not time-out.</li>
          </ul>
        </div>
        <!-- In Progress -->
        <div class="col-md-4">
          <h4>In Progress</h4>
          <ul>
            <li>Integration of Natural Language Processing to help with 
              detecting the type of clue.</li>
            <li>Addition of new solvers including Charade, Deletion, Container and 
              Reversal.</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
  <hr>
  <!-- Iteration 1 -->
  <div class="row">
    <div class="col-md-12">
      <h3>Iteration 1 <small>29th January 2014</small></h3>
      <p>Today marks the completion of the first iteration. Although the solver
      only currently supports a basic set of solvers, there has been plenty of
      work completed so that additional solvers can be <i>hopefully</i> added in
      no time at all.</p>
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
