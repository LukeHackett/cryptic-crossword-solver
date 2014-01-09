<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:crypticpage>
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
      <form class="form-horizontal" role="form">
        <div id="clue-input">
          <div class="form-group">
            <label for="clue" class="col-sm-3 control-label">Cryptic
              Clue</label>
            <div class="col-sm-7">
              <input type="text" class="form-control" id="clue" name="clue">
              <span class="help-block">Punctuation can also be included.</span>
            </div>
          </div>
          <div class="form-group">
            <label for="length" class="col-sm-3 control-label">Solution
              Length</label>
            <div class="col-sm-7">
              <input type="text" class="form-control" id="length" name="length">
              <span class="help-block">Any combination of single words
                (e.g. 3), multiple words (e.g. 3,5) or hyphenated words (e.g.
                3-5) can be entered.</span>
            </div>
          </div>
        </div>
        <hr>
        <div id="clue-pattern" class="form-group">
          <span class="help-block">Provide any known characters below:</span>
          <label for="pattern" class="col-sm-3 control-label">Solution
            Pattern</label>
          <div class="col-sm-7">
            <input type="text" class="form-control" id="pattern"
              name="pattern">
          </div>
        </div>
        <div class="form-actions">
          <button type="reset" class="btn btn-default" id="reset" >Clear</button>
          <button type="submit" class="btn btn-primary" id="submit">Submit</button>
        </div>
      </form>
    </div>
  </div>
</t:crypticpage>
