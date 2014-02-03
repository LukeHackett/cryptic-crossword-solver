/**
 * This jQuery plug-in provides a wrapper around the cryptic form, to ensure 
 * that data is correctly being sent to the server. The plug-in will also handle
 * displaying the results.
 *
 * Author  :  Luke Hackett
 * Date    :  January 2014
 * Version :  1.0 
 */
(function($) {

  $.fn.formHandler = function(options) {

    // Default settings
    var settings = $.extend({
      'presence'      : [],
      'patternHolder' : null,
      'words'         : null,
      'subword'       : null,
      'container'     : null,
      'results'       : null,
      'reset'         : null,
      'blank'         : '?',
      'hyphen'        : '-',
      'separator'     : ',',
      'successClass'  : 'has-success',
      'errorClass'    : 'has-error'
    }, options);

    // Method Listing
    return this.each(function() {
      
      // Validates clue presence - a bit of a hack
      $('#clue').on('keyup', function(){
        inputIsEmpty('#clue');
      });

      // Handles the reset button
      $(settings.reset).on('click', function(evt){
        // Remove any existing alerts
        removeFormAlerts();
        removeResultAlerts();
        // Clear any existing results
        clearResults();
        // Remove any form colour indicators
        $('.form-group').removeClass(function(index, css) {
          return (css.match(/\bhas-\S+/g) || []).join(' ');
        });
      });

      // Form submit event handler
      $(this).on('submit', function (event) {
        event.preventDefault();
        var self = $(this);

        // Check to see if the form has been completed
        if(hasValidationErrors()){
          return;
        } 

        // Merge the individual characters to form the solution pattern
        $(settings.patternHolder).val(createSolutionPattern());

        // Perform the AJAX request
        $.ajax({
          url: self.attr('action'),
          type: 'POST',
          data: self.serialize(),
          dataType: 'json',
          beforeSend: function(){
            // Clear any existing results & alerts
            clearResults();
            removeFormAlerts();
            // Show the loading logo
            showLoading();
          }
        })
        .always(function() {
          // remove loading image
          hideLoading();
          // Initialise the results area
          initialiseResults();
        })
        .done(function(data) {
          // Show the given input values
          showInputValues(data.solver.clue, data.solver.pattern);
          // Ensure returns have been generated
          if(data.solver.solution) {
            // Output the results
            showResults(data.solver.solution);
            // Paginate the results if there are more than 10
            if(data.solver.solution.length > 10) {
              paginateResults();
            }
          } else {
            // No data was returned
            var message = '<b>Heads up!</b> The solvers have been unable to ' +
                'find a solution to your clue. Try widening your solution ' +
                'pattern by using more unknown characters (?).';
            raiseResultAlert(settings.results, 'info', message);
          }
        })
        .fail(function(jqXHR, textStatus, errorThrown) {
          // Display new errors if available
          if(jqXHR.responseJSON){
            // Sanity purposes
            errors = jqXHR.responseJSON.solver.errors;
            // Loop over if array
            if($.isArray(errors)){ 
              // Display each of the error messages
              $.each(errors, function(index, error){  
                message = '<b>Oh snap!</b> ' + error.message;
                raiseFormAlert('danger', message);       
              });           
            } else {
              // Display the error message
              message = '<b>Oh snap!</b> ' + errors.message;
              raiseFormAlert('danger', message); 
            }
          }
        });
      });

      // pagination event handler
      $(settings.results).on('click', '.pagination a', function(event){
        // prevent the default action
        event.preventDefault();
        var self = $(this);
        // Get all solutions
        var solutions = $("#accordion");
        // Hide all solutions
        solutions.children().hide();
        // Show the selected solutions
        solutions.children('[group="' + self.attr('action') + '"]').show();
        // Set the currently selected page
        updatePagination(self.parent());
      });

    });

    /**
     * This function will return whether or not the given input has a non-empty 
     * value. If the input is empty, then true is returned, and the input will
     * turn red. A non-empty input will return false, and the input box will be
     * green.
     */
    function inputIsEmpty(input){
      var errors = false;

      var input = $(input);
      var inputGroup = input.parents('.form-group:first');
      // Ensure there is an input
      if(input.val().trim() == ''){
        // Errors exist
        errors = true;
        // Make the input field go red denoting an error
        swapClass(inputGroup, settings.successClass, settings.errorClass);
      } else {
        // Remove any existing errors
        swapClass(inputGroup, settings.errorClass, settings.successClass);
      }
      
      return errors;
    };

    /**
     * This function will ensure that all inputs denoted within the presence 
     * setting are valid. If any inputs are empty, then true is returned, and 
     * the input will turn red.
     */
    function hasValidationErrors(){
      var errors = false;

      // Loop over all required inputs
      $.each(settings.presence, function(index, input){
        // Check each input for a value
        if(inputIsEmpty(input)){
          errors = true;
        }
      });

      return errors;
    };

    /**
     * This function will swap the 'before' class with the 'after' class upon 
     * the given 'element'.
     */
    function swapClass(element, before, after){
      element.removeClass(before);
      element.addClass(after);
    };

    /**
     * Combines all separate input boxes into a single String, separated by the
     * separated value. Hyphenated words are fully supported, and separated by 
     * the hyphenated separator value.
     */
    function createSolutionPattern(){
      var pattern = '';
      var words = $(settings.words);

      // Loop over each set "word"
      words.each(function(i, word){
        // Loop over each of the individual input boxes
        $(word).children().each(function(){
          if($(this).is('input[type="text"]')){
            // Get the value
            var value = $(this).val().trim();
            // Append the value to the pattern or use the default blank value
            pattern += (value == '') ? settings.blank : value;
          } else {
            // Append the hyphen
            pattern += settings.hyphen;
          }
        });
        // Append the word separator
        if(i != words.length-1){
          pattern += settings.separator;
        }
      });

      return pattern;
    };

    /**
     * This function will create the results area, if it has already not been 
     * added to the DOM.
     */
    function initialiseResults(){
      // Find the results element
      var results = $(settings.results);
      // Remove any existing results
      clearResults;
      // Add the Results header
      results.append($('<h3>').text('Results'));
      // Add the clue received paragraph element
      results.append($('<p>').attr('id', 'clue-received'));
      // Add the pattern received paragraph element
      results.append($('<p>').attr('id', 'pattern-received'));
    };

    /**
     * This function will output the given clue and pattern solution to the 
     * results area of the page
     */
    function showInputValues(clue, pattern){
      // State the clue received
      $('#clue-received').html('<b> Clue:</b> ' + clue);
      // State the clue pattern received
      $('#pattern-received').html('<b> Pattern:</b> ' + pattern);
    };

    /**
     * This function will show a single result object or a list of result 
     * objects in the results area of the page.
     */
    function showResults(results){
      // Add the panel-group to the results DOM
      var group = $('<div>').addClass('panel-group').attr('id', 'accordion');
      $(settings.results).append(group);
      // Loop over if array
      if($.isArray(results)){ 
        // Display each of the solutions
        $.each(results, function(index, solution){
          var id = 'solution' + index;
          createPanel('#accordion', id, solution); 
        });           
      } else {
        // Display the single result
        createPanel('#accordion', 'solution0', results);
      }
      // Set the first result to show the trace path
      $(settings.results).find('.panel-collapse:first').addClass('in');
    };

    /**
     * This function will format a single solution (result) to the result area.
     */
    function createPanel(group, id, solution){
      // The answer solution answer
      var title = $('<h3>').addClass('panel-title').append( 
        $('<a>').attr({
          'data-toggle': 'collapse',
          'data-parent': group,
          'href': '#' + id
        }).text(solution.value)
      );
      // Solution Confidence rating
      var confidence = $('<span>').attr({
        'class': 'label label-default pull-right'
      }).html(solution.confidence + '&#37;');
      // Solution solver that was used
      var solver = $('<span>').attr({
        'class': 'label label-info pull-right'
      }).text(solution.solver);
      // Format the panel header
      var header = $('<div>').addClass('panel-heading');
      header.append(title);
      header.append(confidence);
      header.append(solver);
      // Create the body
      var panelBody = $('<div>').addClass('panel-body');
      var body = $('<div>').attr({
          'id': id,
          'class': 'panel-collapse collapse'
        }).append(panelBody);
      // Show the trace if available
      if(solution.trace != null){
        panelBody.append( $('<p>').text('Solution Trace:') );
        // Add the ordered list
        panelBody.append( $('<ol>') );
        // Each each trace as part of the ordered list
        $.each(solution.trace, function(i, step){
          panelBody.find('ol').append( $('<li>').text(step) );
        });
      } else {
        // Trace is not available
        panelBody.append( $('<p>').text('Solution Trace Unavailable.') );
      }
      // create the final panel
      var panel = $('<div>').addClass('panel panel-default');
      panel.append(header);
      panel.append(body);
      // Add the panel to the DOM
      $(group).append(panel);
    };
    
    /**
     * This function will paginate all the results that is found within the 
     * #accordion element.
     */
    function paginateResults(){
      // Number of results per page
      var resultsPerPage = 10;
      // list of results
      var solutions = $("#accordion").children();
      var noPages = Math.ceil(solutions.length / resultsPerPage);
      // Start and End selector values
      var start = 0;
      var end = resultsPerPage;
      // Create a new Pagination
      var ul = $('<ul>').addClass('pagination');
      // Assign each block of results to a page
      for(var i = 0; i < noPages; i++){
        // Increment the counter by one for aesthetic purposes
        var j = i + 1;
        // Only show the first set of results by default
        if(i == 0){
          solutions.slice(start, end).attr('group', j);
        } else {
          solutions.slice(start, end).attr('group', j).hide();
        }
        // Add counter to the main paginate
        ul.append( $('<li>').append( 
            $('<a>').attr({
              'href': '#', 
              'action': j
            }).text(j) 
          ) 
        );
        // Move onto the next block of results
        start += resultsPerPage;
        end += resultsPerPage;
      }
      // Add the first button
      ul.prepend( $('<li>').append(
          $('<a>').attr({
            'href': '#', 
            'action': 1
          }).html('&laquo;')
        ) 
      );
      // Add the last button
      ul.append( $('<li>').append(
          $('<a>').attr({
            'href': '#', 
            'action': noPages
          }).html('&raquo;')
        ) 
      );
      // Add the paginate to the main body
      $(settings.results).append($('<div>').addClass('text-center').append(ul));
      // Show the first page on the pagination
      updatePagination(ul.children().first());
    };

    /**
     * This function will set the given li element to the currently view page, 
     * ensuring that all other pages are not selected. The .active and .disabled
     * classes are added to the given element to indicate the current page.
     */
    function updatePagination(li){
      // Get the index of the selected page
      var index = li.index();
      // List of pages
      var siblings = $(li.siblings());
      // Number of pages
      var length = siblings.length;
      // Remove the css classes from all siblings
      siblings.removeClass('active disabled');
      // Set the currently selected number
      li.addClass('active disabled');
      // Check to see if the last and next buttons should be disabled
      if(index == 0 || index == length-1) {
        li.next().addClass('active disabled');
      } else if(index == length || index == 1) {
        li.prev().addClass('active disabled');
      }
    };

    /**
     * This function shows the loading results message.
     */
    function showLoading(){
      // The progress bar
      var progressbar = $('<div>').attr({
        'class': 'progress-bar progress-bar-success',
        'role': 'progressbar',
        'aria-valuenow': '100',
        'aria-valuemin': '100',
        'aria-valuemax': '100',
        'style': 'width: 100%'
      });
      // Adding screen reader support
      progressbar.append( $('<span>').addClass('sr-only').text('Processing Request') );
      // The progress bar wrapper
      var barWrap = $('<div>').addClass('progress progress-striped active');
      barWrap.append(progressbar);
      // Inform the user what is currently happening
      var info = $('<h3>').text('Obtaining the results, please wait...');
      // Create the main wrapper
      var loading = $('<div>').attr({
        'id': 'loading',
        'class': 'well'
      });
      loading.append(info);
      loading.append(barWrap);
      // Add to the DOM
      $(settings.results).append(loading);
    };

    /**
     * This function removes the loading results message from the DOM
     */
    function hideLoading(){
      $(settings.results).find("#loading").remove();
    };

    /**
     * Creates a new result alert with the type being one of (success, info, 
     * warning, danger), and the message to be displayed.
     */
    function raiseResultAlert(area, type, message){
      $(settings.results).append($('<div>').addClass('alert alert-' + type).html(message));
    };

   /**
     * Creates a new form alert with the type being one of (success, info, 
     * warning, danger), and the message to be displayed.
     */
    function raiseFormAlert(type, message){
      $('#solver').prepend($('<div>').addClass('alert alert-' + type).html(message));
    };

    /**
     * Removes all alerts from the results area
     */
    function removeResultAlerts(){
      $(settings.results).find('.alert').remove();
    };

    /**
     * Removes all alerts from the form area
     */
    function removeFormAlerts(){
      $('#solver').find('.alert').remove();
    };

    /**
     * This method will clear all results and alerts from the results area. 
     */
    function clearResults(){
      $(settings.results).children().remove();
    };
    
  };
}(jQuery));