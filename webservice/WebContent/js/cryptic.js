jQuery(document).ready(function($){
  var BLANK = "?";
  var HYPHEN = "-";
  var SEPARATOR = ",";
  
  
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
    
    // Do nothing if empty
    if(pattern === ""){
      return;
    }
    
    // Ensure the user has entered a valid string
    if(!valid_solution_pattern(pattern)){
      // Make text input red denoting an error
      self.parents(".form-group").addClass("has-error");
      return;
    }
    
    // Remove the error class
    self.parents(".form-group").removeClass("has-error");
    
    // Show the clue pattern input area
    $("#clue-split-pattern").show();
    
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
    var self = $(this);
    // Special key's event codes
    var special_keys = [8, 9, 13, 27, 46];

    // Allow special chars + arrows only 
    if (special_keys.indexOf(event.keyCode) != -1 
        || (event.keyCode == 65 && event.ctrlKey === true) 
        || (event.keyCode >= 35 && event.keyCode <= 39)){
          return;
    
    } else if(event.keyCode == 109 || event.keyCode == 188 || event.keyCode == 189) {
      // Get the last character of the string
      var last = self.val().charAt(self.val().length-1);
      // Prevent input if it's the same character
      if (last == "," || last == "-") {
        event.preventDefault();
      } else {
        return;
      }
    
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
  // Clear the input area
    clean_pattern_input();
    
    // Clear the results area
    $('#clue_recieved').html("");
    $('#pattern_recieved').html("");
    $('#results').children().remove();
  });

  
  /**
   * On click event handler for the submit button
   */
  $('#submit').on('click', function(evt){
    var self = $(this);
    var form = self.parents("form");
    
    // Prevent default actions
    evt.preventDefault();
    
    // Validate input
    if(inputs_have_errors()){
      return;
    }
    
    // Combine the pattern
    pattern = combine_pattern();
    $('#pattern').val(pattern);
    
    // Do some AJAX fancy magic 
    $.ajax({
      url: form.attr("action"),
      type: 'POST',
      data: form.serialize(),
      dataType: "json",
      success: function(data) {
        // State the clue received
        $('#clue_recieved').html("<b> Clue:</b> " + data.clue);
        
        // State the clue pattern received
        $('#pattern_recieved').html("<b> Pattern:</b> " + data.pattern);
        
        // Remove old results
        var results = $('#results');
        results.children().remove();

        // Print out each of the solutions
        $.each(data.solution, function(index, solution){
          // Format a new row
          var row = "Answer: " + solution.value + 
                    " (" + solution.confidence + "%)";
          // Append to the text area
          results.append("<li>" + row + "</li>");
        });       
      },
      fail: function(jqXHR, textStatus) {
        // TODO: Error handling
        console.log( "Request failed: " + textStatus );
      }
    });
  });
  
  
  /**
   * Returns whether or not the form has input errors.
   */
  function inputs_have_errors(){
    var clue = $('#clue');
    var length = $('#length');
    var error = true;
  
    // Check to ensure the clue has been input
    if(clue.val() == ""){
      clue.parents(".form-group").addClass("has-error");
      error = true;
    } else {
      clue.parents(".form-group").removeClass("has-error");
    }
    
    // Check to ensure solution length has been input
    if(length.val() == "" || !valid_solution_pattern(length.val())){
      length.parents(".form-group").addClass("has-error");
      error = true;
    } else {
      length.parents(".form-group").removeClass("has-error");
      error = false;
    }
  
    return error; 
  }
  

  /**
   * Combines all separate input boxes into a single String, separated by the
   * separated value. Hyphenated words are fully supported, and separated by the
   * hyphenated separator value.
   */
  function combine_pattern(){
    var pattern = "";
    var words = $('#clue-split-pattern .word');
    
    // Get each character and combine
    words.each(function(i, row){
      var self = $(row);
      var subwords = self.children('.sub-word');
      
      if(subwords.length == 0){
        // Single Word
        pattern += input_to_pattern(self);          
      } else {
        // Hyphen word
        subwords.each(function(s, subword){
          // Append the new words to the pattern
          pattern += input_to_pattern($(subword));
          // Append a hyphen if required
          if(s != subwords.length-1){
            pattern += HYPHEN;
          }
        });
      }
      
      // Append the word separator if required
      if(i != words.length-1){
        pattern += SEPARATOR;
      }
        
    });
      
    return pattern;
  }
  

  /**
   * Merges a div of input box values into a a single string value
   */
  function input_to_pattern(div){
    var pattern = '';
    
    // Loop over all inputs
    div.find('input[type="text"]').each(function(i, input){
      var value = input.value;
      
      // Replace with required blank value  
      if(value == "" || value == " "){
        pattern += BLANK;
      } else {
        pattern += value;
      }
    });
  
    return pattern;
  }
  
  
  /**
   * Removes all unneeded input boxes, and hides the entire wrapping div 
   */
  function clean_pattern_input(){
    // Hide and disable original input
    $("#clue-pattern").hide();
    $('#clue-pattern input[type="text"]').attr("readonly", "true");
    
    // Remove all content (apart from span)
    $("#clue-split-pattern > :not(span)").remove();
    
    // hide what's left (the span)
    $("#clue-split-pattern").hide();    
  }

  
  /**
   * Returns whether or no the given solution length input is valid or not. 
   * This is based upon whether or not the solution length is 15 characters 
   * or less, and if it matches the correct solution pattern
   */
  function valid_solution_pattern(input){
    // Check to ensure the solution pattern is correctly entered
    var compare = input.match(/[0-9]+((,|-)[0-9])*/gi);
    var pattern_match = compare[0] == input;
    
    // Get all numbers
    var compare2 = input.match(/[0-9]+/gi);
    
    // Check to ensure each number is less than 15
    var largest = Math.max.apply(Math, compare2);
    var largest_match = largest <= 15;
    
    // Check to ensure each number is larger than 0
    var smallest = Math.min.apply(Math, compare2);
    var smallest_match = smallest > 0;
    
    // Check for matches
    return pattern_match && largest_match && smallest_match;
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
      $('#clue-split-pattern').append('<div class="word"></div>');
      pattern = $('#clue-split-pattern .word').last();
      
      // Check for a hyphenated word 
      if($.isArray(word)){        
      // Loop over each sub-hyphenated word
        for(var h = 0; h < word.length; h++){     
          pattern.append('<div class="sub-word"></div>');  
          subpattern = pattern.children('.sub-word').last();
            
          // Append the label to the form
          var label = $("<label>").addClass("col-sm-3 control-label");
          label.text("word " + w + ", sub-word " + h);
          subpattern.append(label);
          
          // Create each of the text inputs
          for(var c = 0; c < word[h]; c++){
            // Create the text input
            var input = $('<input type="text">').attr({maxlength: '1', size: '1'});
            
            // Append the text input to the
            subpattern.append(input);
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
          var input = $('<input type="text">').attr({maxlength: '1', size: '1'});
          
          // Append
          pattern.append(input);
        }
        // TODO: Currently a hack. Will need to CSS this baby up.
        pattern.append("<br><br>");
      }
    }
  }
});