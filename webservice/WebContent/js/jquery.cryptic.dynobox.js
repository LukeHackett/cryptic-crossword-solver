/**
* Provides a simple jQuery plug-in to allow a given number of text boxes to be 
* dynamically created.
*
* Author  :  Luke Hackett
* Date    :  January 2014
* Version :  1.0 
*/
(function($) {

  $.fn.dynoBox = function(container, options) {

    // Default settings
    var settings = $.extend({
      "regex"         : null,
      "inclusions"    : null,
      "maxWordLength" : 15,
      "minWordLength" : 1,
      "successClass"  : "has-success",
      "failureClass"  : "has-error"
    }, options);

    // Reference to self (text input)
    var self = $(this);

    // Method Listing
    return this.each(function() {

      // Validates the input on keypress
      self.on('keypress', function (event){
        if(!validateNumeric(event)){
          event.preventDefault();
        }
      });

      // Validates against the given regular expression
      self.on('keyup', function (event) {
        // Sanity variables
        var input = this.value;
        var self = $(this);
        var wrapper = $(container);
        // Ensure settings are available
        settings.inclusions = settings.inclusions || [];        
        // Ensure the minimum inputs have been given
        if(input == ""){
          wrapper.children().remove();
          // Add the error class if defined
          if(settings.failureClass){
            self.parents(".form-group").removeClass(settings.successClass);
            self.parents(".form-group").addClass(settings.failureClass);
            wrapper.children().remove();
          }
          return;
        }
        // Match the input to the regular expression
        if(!isValidSolutionPattern(input)){
          // Add the error class if defined
          if(settings.failureClass){
            self.parents(".form-group").removeClass(settings.successClass);
            self.parents(".form-group").addClass(settings.failureClass);
            wrapper.children().remove();
          }
          return;
        }
        // Remove the error class (if required)
        if(settings.failureClass){
          self.parents(".form-group").removeClass(settings.failureClass);
          self.parents(".form-group").addClass(settings.successClass);
        }
        // clear the container
        wrapper.children().remove();
        // Split based upon comma separator
        var words = splitSolution(input);
        // Make the HTML
        createInputGroup(container, words);
        // Bind auto tab to the newly created inputs
        $(".word input[type=text]").autotab({ format: 'alpha' });
      });  
    });

    /**
     * This method returns whether or not the given event.which keycode can be 
     * classified as a control key - i.e. backspace, tab, enter, end, home, 
     * left or right.
     */
    function isControlKey(key){
      // Backspace, tab, enter, end, home, left, right
      // We don't support the del key in Opera because del == . == 46.
      var controlKeys = [8, 9, 13, 35, 36, 37, 39];
      // IE doesn't support indexOf
      return controlKeys.join(",").match(new RegExp(key));
    };

    /**
     * This method will validate the given event, based upon the last pressed 
     * key. If the key is deemed to be a valid numeric value, or a control key 
     * or a key code within the "inclusions" list then it will pass the test. 
     * All other key codes will fail.
     */
    function validateNumeric(event){
      // Some browsers just don't raise events for control keys.
      // e.g. Safari backspace, so use standardise control keys instead
      if (!event.which ||
          // Allow numbers 1 - 9
          (49 <= event.which && event.which <= 57) || 
          // Disallow 0 as first digit 
          (48 == event.which && self.val()) ||     
          // Allow any given inclusions
          ($.inArray(event.which, settings.inclusions) >= 0) ||
          // Opera assigns values for control keys.
          isControlKey(event.which)) { 
        return true;
      } else {
        return false;
      }
    };

    /**
     * Returns whether or not the given solution length input is valid or not. 
     * This is based upon whether or not the solution length is 15 characters 
     * or less, and if it matches the correct solution pattern.
     */
    function isValidSolutionPattern(input){
      // Check to ensure the solution pattern is correctly entered
      var compare = input.match(settings.regex);
      var pattern_match = compare[0] == input;
      // Get all numbers
      var compare2 = input.match(/[0-9]+/gi);
      // Check to ensure each number is less than 15
      var largest = Math.max.apply(Math, compare2);
      var largest_match = largest <= settings.maxWordLength;
      // Check to ensure each number is larger than 0
      var smallest = Math.min.apply(Math, compare2);
      var smallest_match = smallest >= settings.minWordLength;
      // Check for matches
      return pattern_match && largest_match  && smallest_match;
    };

    /**
     * Splits a valid solution length into an array of integer values.
     */
    function splitSolution(length){
      // Split based upon comma separator
      var words = length.split(",");
      // Find any hyphenated words
      for(var i = 0; i < words.length; i++){
        var element = words[i];
        // Split the elements if hyphenated
        if(element.indexOf("-") != -1){
          var sub_words = element.split("-");
          words[i] = sub_words;
        }
      }
      return words;
    };

    /**
     * This function will create a number of text input boxes, based upon the 
     * overall data structure. The function heavily uses the createInputBoxes 
     * function, and will return a well-formed jQuery object.
     */
    function createInputGroup(container, words){
      // jQueryfiy the container
      var container = $(container);
      // Create each "word holder"
      for (var i = 0; i < words.length; i++){ 
        // Get the current word's solution length
        var solution = words[i];
        // create the main form-group wrapper
        var wordForm = $('<div>').addClass('form-group');
        // Word Number
        var wordNum = 'Word ' + (parseInt(i) + 1);
        // Word label template
        var label = $('<label>').addClass('col-sm-2 control-label').text(wordNum);
        // Create the inputs and hypens if required
        var input = createInputBoxes(solution);
        // Make the final append
        wordForm.append(label);
        wordForm.append(input);
        container.append(wordForm);
      }
    };

    /**
     * This builder function will create a number of text input boxes, based 
     * upon the given solution data structure. If an array is passed, then a 
     * hyphenated word input will be generated, where as a single integer value
     * will generate a single word input to be created.
     */
    function createInputBoxes(solution){
      // Word container template
      var wordContainer = $('<div>').addClass('col-sm-10 word');
      // Character input template
      var input = $('<input type="text">').addClass('form-control').attr({maxlength: '1', size: '1'});
      // Hyphenated word separator
      var hypen = $('<p>').addClass('form-control-static').text(' - ');
      // Check for Hyphenated word
      if($.isArray(solution)){
        // Is a Hyphenated word
        for(var i = 0; i < solution.length; i++){
          var noInputs = parseInt(solution[i]);
          // Create the required number of char input boxes
          for(var j = 0; j < noInputs; j++){
            wordContainer.append(input.clone());
          }
          // Add a hyphenate word separator
          if(i+1 != solution.length){
            wordContainer.append(hypen.clone());
          }
        }
      } else {
        // Create the required number of char input boxes
        for(var i = 0; i < solution; i++){
          wordContainer.append(input.clone());
        }
      }
      return wordContainer;
    };

  };
}(jQuery));