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
        $(".form-group").removeClass(function(index, css) {
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
          // Ensure returns have been generated
          if(data.solver.solution) {
             // Show the given input values
            showInputValues(data.solver.clue, data.solver.pattern);
            // Output the results
            showResults(data.solver.solution);
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
      var inputGroup = input.parents('.form-group:first')
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
      // Add the possible solution results list
      results.append($('<ul>').attr({'id': 'results-list', 'class': 'list-group'}));
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
      // Loop over if array
      if($.isArray(results)){ 
        // Display each of the solutions
        $.each(results, function(index, solution){
          createPanel(solution); 
        });           
      } else {
        // Display the single result
        createPanel(results);
      }
    };

    /**
     * This function will format a single solution (result) to the result area.
     */
    function createPanel(solution){
      // Solution Confidence rating
      var span = '<span class="badge">' + solution.confidence + ' &#37;</span>';
      // List Element
      var li = $('<li>').addClass('list-group-item');
      li.html(span + solution.value);
      // Append to the text area
      $('#results-list').append(li);
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
  }
}(jQuery));