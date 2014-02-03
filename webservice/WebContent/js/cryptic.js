jQuery(document).ready(function($){

  // Remove the non-js pattern input style
  initialisePatternInput();

  /**
   * Multi-event handler that provides hiding and showing of the helper blocks
   * for the given input box
   */
  $('#clue-input input[type="text"]').hidenseek();

  /**
   * Multi-event handler for the length input text box
   */
  $('#length').dynoBox('#clue-split-pattern', {
      'regex'      : /[0-9]+((,|-)[0-9]+)*/gi,     // Solution length Regex 
      'inclusions' : [44, 45]                      // Commas and Hyphens
  });

  /**
   * Reset button event handler
   */
  $('#reset').on('click', function(evt){
    // Clear the split-pattern input area
    initialisePatternInput();
  });

  /**
   * Form submit event handler
   */
  $('#solver').formHandler({
    'presence'      : ['#clue', '#length'],
    'patternHolder' : '#pattern',
    'words'         : '#clue-split-pattern .word',
    'subword'       : '.sub-word',
    'container'     : '#container',
    'results'       : '#results',
    'reset'         : '#reset'
  });

  /**
   * Removes all unneeded input boxes, and hides the entire wrapping div 
   */
  function initialisePatternInput(){
    // Hide and disable original input
    $('#clue-pattern').hide();
    $('#clue-pattern input[type="text"]').attr('readonly', 'true');
    
    // Remove all content within the split-pattern
    $('#clue-split-pattern').children().remove(); 
  }

});