jQuery(document).ready(function($){
  
  // Remove the non-js pattern input style
  clean_pattern_input();

  // Hide the input helpers by default
  $('#clue-input input[type="text"]').next().hide();
  
  
  /**
   * Event handler for the an input text box gaining and losing focus.
   */
  $('#clue-input input[type="text"]')
    .focusin(function() {
      $(this).next().slideDown(250);
    }).focusout(function() {
      $(this).next().slideUp(250);
    });
  
  
  /**
   * Key event handler for when the solution length is input.
   */
  $('#length').keyup(function(evt){
    var self = $(this);
    var pattern = self.val();

    // Clean up
    clean_pattern_input();

    // Ensure the user has entered a valid string
    if(pattern === "" || !valid_solution_pattern(pattern)){
      // Make text input red denoting an error
      self.parents(".form-group").addClass("has-error");
      return;
    }
    
    // Remove the error class
    self.parents(".form-group").removeClass("has-error");
    
    // Show the clue pattern input area
    $("#clue-pattern").show();
    
    // Create a 'pattern model'
    pattern_model = split_solution(pattern);

    // Show the pattern model
    create_pattern_input(pattern_model);
  });

  
  /**
   * Prevents the user from typing non-numerical characters into the solution 
   * length input box.
   */
  $('#length').keydown(function(event){
    // Special key's event codes (inc. commas=188, dashes=109)
    var special_keys = [8, 9, 13, 27, 46, 109, 188];
    
    // Allow special chars + arrows only 
    if (special_keys.indexOf(event.keyCode) != -1 
        || (event.keyCode == 65 && event.ctrlKey === true) 
        || (event.keyCode >= 35 && event.keyCode <= 39)){
            return;
    } else {
      // Reject all other keys (except numbers)
      if (event.shiftKey || (event.keyCode < 48 || event.keyCode > 57) 
          && (event.keyCode < 96 || event.keyCode > 105 )) {
            event.preventDefault(); 
      }   
    }
  });
  
  
  /**
   * On click event handler for the reset button.
   */
  $('#reset').on('click', function(evt){
    clean_pattern_input();
  });

  
  /**
   * On click event handler for the submit button
   */
  $('#submit').on('click', function(evt){
    // Prevent default actions
    evt.preventDefault();
    
    // Validate input
    if(inputs_have_errors()){
      return;
    }
    
    // TODO Send form to the server
  });
  
  
  /**
   * Returns whether or not the form has input errors.
   */
  function inputs_have_errors(){
    var clue = $('#clue');
    var length = $('#length');
    var error = false;
  
    // Check to ensure the clue has been input
    if(clue.val() == ""){
      clue.parents(".form-group").addClass("has-error");
      error = true;
    } else {
      clue.parents(".form-group").removeClass("has-error");
    }
    
    // Check to ensure solution length has been input
    if(length.val() == "" || !valid_solution_pattern(pattern)){
      length.parents(".form-group").addClass("has-error");
      error = false;
    } else {
      length.parents(".form-group").removeClass("has-error");
      error = true;
    }
  
    return error; 
  }
  
  /**
   * TODO: Complete method.
   *       - add word separators
   */
  function combine_pattern(){
    var pattern = "";
    
    // Get each character and combine
    $('#clue-pattern label[for!="pattern"]').each(function(i, input){
      pattern += $(input).val();
    });
  }
  
  
  /**
   * Removes all unneeded input boxes, and hides the entire wrapping div 
   */
  function clean_pattern_input(){
    // Remove all content (apart from span)
    $("#clue-pattern > :not(span)").remove();
    
    // hide what's left (the span)
    $("#clue-pattern").hide();    
  }

  
  /**
   * Returns whether or no the given solution length input is valid or not. 
   * This is based upon whether or not the solution length is 15 characters 
   * or less, and if it matches the correct solution pattern
   */
  function valid_solution_pattern(input){
    // Check to ensure the solution pattern is correctly enter
    var compare = input.match(/[1-9]+((,|-)[1-9])*/gi);
    var pattern_match = compare[0] == input;
    
    // Check to ensure each number is less than 15
    var compare2 = input.match(/[1-9]+/gi);
    var largest = Math.max.apply(Math, compare2);
    var largest_match = largest <= 15;
    
    // Check for matches
    return pattern_match && largest_match;
  }

  
  /**
   * Splits a valid solution length into an array of integer values
   */
  function split_solution(length){
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
  }
  

  /**
   * Creates a series of text input boxes, based upon the clue solution 
   * pattern - i.e. whether or not it is a hyphenated word, how many letters 
   * are required for each word etc.
   */
  function create_pattern_input(pattern_model){
    // Loop over each word
    for(var w = 0; w < pattern_model.length; w++){
      var word = pattern_model[w];

      // Easy access
      var pattern = $('#clue-pattern');

      // Check for a hyphenated word 
      if($.isArray(word)){
        // Loop over each sub-hyphenated word
        for(var h = 0; h < word.length; h++){     
          // Append the label to the form
          var label = $("<label>").addClass("col-sm-3 control-label");
          label.text("word " + w + ", sub-word " + h);
          pattern.append(label);
          
          // Create each of the text inputs
          for(var c = 0; c < word[h]; c++){
            // Create the text input
            var name = 'w' + w + 'h' + h + 'c' + c;
            var input = $('<input type="text">').attr({
              maxlength: '1', 
              size: '1',
              name: name, 
              id: name  
            });
            
            // Append the text input to the
            pattern.append(input);
          }
          // TODO: Currently a hack. Will need to CSS this baby up.
          pattern.append("<br><br>");
        }
        
      } else { // not a hyphenated word
        
        // Append the label to the form
        var label = $("<label>").addClass("col-sm-3 control-label");
        label.text("word " + w);
        pattern.append(label);

        for(c = 0; c < word; c++){
          // Create a new text input  
          var name = 'w' + w + '[' + c + ']';
          var input = $('<input type="text">').attr({
            maxlength: '1', 
            size: '1',
            name: name, 
            id: name  
          });
          
          // Append
          pattern.append(input);
        }
        // TODO: Currently a hack. Will need to CSS this baby up.
        pattern.append("<br><br>");
      }
    }
  }
});