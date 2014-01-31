/**
 * This jQuery plug-in provides a wrapper around the cryptic form, to ensure 
 * that data is correctly being sent to the server.
 *
 * Author  :  Luke Hackett
 * Date    :  January 2014
 * Version :  1.0 
 */
(function($) {

  $.fn.formHandler = function(options, callback) {

    // Default settings
    var settings = $.extend({
      "presence"      : [],
      "patternHolder" : null,
      "words"         : null,
      "subword"       : null,
      "blank"         : "?",
      "hyphen"        : "-",
      "separator"     : ",",
      "successClass"  : "has-success",
      "errorClass"    : "has-error"
    }, options);

    // Method Listing
    return this.each(function() {
      
      // Validates clue presence - a bit of a hack
      $('#clue').on('keyup', function(){
        inputIsEmpty("#clue");
      });

      // Form submit event handler
      $(this).on('submit', function (event) {
        event.preventDefault();

        // Check to see if the form has been completed
        if(hasValidationErrors()){
          return;
        } 

        // Merge the individual characters to form the solution pattern
        $(settings.patternHolder).val(createSolutionPattern());

        // Make an AJAX request
        console.log("AJAX Request")
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
      var inputGroup = input.parents(".form-group:first")
      // Ensure there is an input
      if(input.val().trim() == ""){
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
        console.log(input);
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
      var pattern = "";
      var words = $(settings.words);
      
      // Get each character and combine
      words.each(function(i, row){
        var self = $(row);
        var subwords = self.children(settings.subword);
        
        if(subwords.length == 0){
          // Single Word
          pattern += wordToPattern(row);          
        } else {
          // Hyphen word
          subwords.each(function(s, subword){
            // Append the new words to the pattern
            pattern += wordToPattern(subword);
            // Append a hyphen if required
            if(s != subwords.length-1){
              pattern += settings.hyphen;
            }
          });
        }
        
        // Append the word separator if required
        if(i != words.length-1){
          pattern += settings.separator;
        }
          
      });
        
      return pattern;
    };
    
    /**
     * Merges a div of input box values into a a single string value
     */
    function wordToPattern(div){
      var pattern = '';
      
      // Loop over all inputs
      $(div).find('input[type="text"]').each(function(i, input){
        var value = input.value;
        // Replace with required blank value  
        if(value == "" || value == " "){
          pattern += settings.blank;
        } else {
          pattern += settings.value;
        }
      });
    
      return pattern;
    };

  }
}(jQuery));