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
          showInputValues(data.solver.clue, data.solver.pattern, 
                          data.solver.total, data.solver.duration);
          // Ensure returns have been generated
          if(data.solver.solution) {
            // Output the results
            showResults(data.solver.solution);
            // Paginate the results if there are more than 10
            if(data.solver.solution.length > 10) {
              paginateResults();
              var message = 'Try supplying known characters to reduce the ' + 
                            'number of possible solutions.';
              raiseResultAlert('info', 'Heads up!', message);
            }
          } else {
            // No data was returned
            var message = 'The solvers have been unable to find a solution to ' +
                          'your clue. Try widening your solution pattern by ' +
                          'sing more unknown characters (?).';
            raiseResultAlert('info', 'Heads up!', message);
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
                raiseFormAlert('danger', 'Oh snap!', error);       
              });           
            } else {
              // Display the error message
              raiseFormAlert('danger', 'Oh snap!', errors.message); 
            }
          }
        });
      });

      // next pagination event handler
      $(settings.results).on('click', '.pagination #next', function(event){
        // prevent the default action
        event.preventDefault();
        // Obtain the current page
        var page = parseInt($('#accordion :not(:hidden):first').attr('group'));
        // Move to the next page
        updatePagination(page + 1);
      });

      // previous pagination event handler
      $(settings.results).on('click', '.pagination #prev', function(event){
        // prevent the default action
        event.preventDefault();
        // Obtain the current page
        var page = parseInt($('#accordion :not(:hidden):first').attr('group'));
        // Move to the previous page
        updatePagination(page - 1);
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
      clearResults();
      // Add the Results header
      results.append($('<h3>').text('Results'));
      // Create a main wrapper around the static UI
      var wrapper = $('<div>').addClass('form-horizontal');
      // Add the clue received paragraph element
      wrapper.append(createStaticForm('Clue', '', 'clue-received'));
      // Add the pattern received paragraph element
      wrapper.append(createStaticForm('Pattern', '', 'pattern-received'));
      // Add the pattern received paragraph element
      wrapper.append(createStaticForm('', '', 'duration'));
      // Append the wrapper to the results element
      results.append(wrapper);
      // Add the horizontal rule separator;
      results.append($('<hr>'));
    };

    function createStaticForm(label, content, id){
      // Create the label
      var lbl = $('<label>').addClass('col-sm-2 control-label').text(label);
      // Create the paragraph
      var value = $('<div>').addClass('col-sm-10').html(
                    $('<p>').attr({
                      'id': id, 
                      'class': 'form-control-static'
                    }).text(content)
                  );
      // Combine together
      return $('<div>').addClass('form-group').append(lbl).append(value);
    }

    /**
     * This function will output the given clue and pattern solution to the 
     * results area of the page
     */
    function showInputValues(clue, pattern, total, duration){
      // State the clue received
      $('#clue-received').text(clue);
      // State the clue pattern received
      $('#pattern-received').text(pattern);
      $('#duration').text(total + ' solutions generated in ' + duration + ' seconds');
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
        // Get the highest confidence rating
        var topConfidence = results[0].confidence;
        // Display each of the solutions
        $.each(results, function(index, solution){
          var id = 'solution' + index;
          createPanel('#accordion', id, solution);
          // Set the panel colour
          setPanelColour('#accordion', topConfidence, solution.confidence);
        });           
      } else {
        // Display the single result
        createPanel('#accordion', 'solution0', results);
        // Set the panel colour (NOTE: only one solution)
        setPanelColour('#accordion', solution.confidence, solution.confidence);
      }
      // Set the first result to show the trace path
      $(settings.results).find('.panel-collapse:first').addClass('in');
      // Enable tooltips
      $('#accordion .label').tooltip();
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
        'class': 'label label-default pull-right',
        'data-toggle': 'tooltip',
        'title': 'The overall confidence rating'
      }).html(solution.confidence + '&#37;');
      // Solution solver that was used
      var solver = $('<span>').attr({
        'class': 'label label-info pull-right',
        'data-toggle': 'tooltip',
        'title': 'The solver that provided the solution'
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
        // Add the solution trace to the DOM
        if($.isArray(solution.trace)) {
          // Each each trace as part of the ordered list
          $.each(solution.trace, function(i, step){
            panelBody.find('ol').append( $('<li>').text(step) );
          });
        } else {
          panelBody.find('ol').append( $('<li>').text(solution.trace) );
        }
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
     * This function will set the panel colour of the last panel in the group
     * based upon the supplied top confidence rating and the confidence rating
     * of the panel.
     */
    function setPanelColour(group, topConfidence, confidence){
      // Obtain the last result added
      var result = $(group).children().last();
      // Calculate the size of the three categories based upon the top rating
      var divider = Math.floor(1/3 * topConfidence);
      // Calculate each of the category boundaries
      var success = topConfidence - divider;
      var warning = topConfidence - (divider * 2);
      var error   = topConfidence - (divider * 3);
      // Set the colour of the panel based upon the confidence rating
      if(confidence == topConfidence) {
        // Top Answer
        result.addClass('panel-primary');
        result.find('span:first').addClass('label-success');
      } else if(confidence >= success) {
        // Highly likely answer
        result.addClass('panel-success');
        result.find('span:first').addClass('label-success');
      } else if(confidence >= warning) {
        // Possibly likely answer
        result.addClass('panel-warning');
        result.find('span:first').addClass('label-warning');
      } else {
        // Unlikely answer
        result.addClass('panel-danger');
        result.find('span:first').addClass('label-danger');
      }
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
        // Move onto the next block of results
        start += resultsPerPage;
        end += resultsPerPage;
      }
      // Create the Pagination
      var ul = $('<ul>').addClass('pagination');
      // Previous page button
      ul.append( $('<li>').attr('id', 'prev').addClass('disabled').append(
          $('<a>').attr('href', '#').html('&larr; Previous')
        )
      );
      // Current page indicator
      ul.append( $('<li>').append(
          $('<a>').html('Page <b id="pgno">1</b> of <b id="totno">' + noPages + '</b>')
        )
      );  
      // Next page button   
      ul.append( $('<li>').attr('id', 'next').append(
          $('<a>').attr('href', '#').html('Next &rarr;')
        )
      );
      // Add the paginate to the main body
      $(settings.results).append($('<div>').addClass('text-center').append(ul));
      // Show the first page on the pagination
    };

    /**
     * This function will set the given li element to the currently view page, 
     * ensuring that all other pages are not selected. The .active and .disabled
     * classes are added to the given element to indicate the current page.
     */
    function updatePagination(newPage){
      // Get the total number of pages
      var total = parseInt($('.pagination #totno').text());
      // Change pages if new page is a valid page number
      if(newPage >= 1 && newPage <= total) {
        var results = $("#accordion");
        var next = $('.pagination #next');
        var prev = $('.pagination #prev');
        // Update the next and previous buttons
        if(newPage == 1){
          prev.addClass('disabled');
          next.removeClass('disabled');
        } else if(newPage == total) {
          next.addClass('disabled');
          prev.removeClass('disabled');
        } else {
          next.removeClass('disabled');
          prev.removeClass('disabled');
        }
        // hide all results
        results.children().hide();
        // Show only 'newPage' results
        results.children('[group=' + newPage + ']').show();
        // Update the current page
        $('.pagination #pgno').text(newPage);
      }
    };

    /**
     * This function shows the loading results message.
     */
    function showLoading(){
      // Show the pre-defined model window
      $('#loading').modal({
        'backdrop': 'static',
        'keyboard': false,
        'show': true
      });
    };

    /**
     * This function hides the loading results message.
     */
    function hideLoading(){
      $('#loading').modal('hide');
    };

    /**
     * Creates a new result alert with the type being one of (success, info, 
     * warning, danger), and the message to be displayed.
     */
    function raiseResultAlert(type, title, message){
      $(settings.results + ' .form-horizontal').append(
        $('<div>').addClass('alert alert-' + type)
                  .html('<b>' + title + '</b> ' + message)
      );
    };

   /**
     * Creates a new form alert with the type being one of (success, info, 
     * warning, danger), and the message to be displayed.
     */
    function raiseFormAlert(type, title, message){
      $('#solver').prepend(
        $('<div>').addClass('alert alert-' + type)
                  .html('<b>' + title + '</b> ' + message)
      );
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