/**
 * This bootstrap plug-in allows for a given input box's following helper-block
 * span element to be shown upon gaining focus, and hiding when the input box
 * has lost focus.
 *
 * Author  :  Luke Hackett
 * Date    :  January 2014
 * Version :  1.0 
 */
(function($) {

  $.fn.hidenseek = function() {

    // Method Listing
    return this.each(function(){

      // Hides the helper-block span by default
      $(this).next().css('visibility', 'hidden');

      // Shows the helper-block span on focus gained
      $(this).focusin(function() {
        $(this).next().css('visibility', 'visible');
      });

      // Hides the helper-block span on focus lost
      $(this).focusout(function() {
        $(this).next().css('visibility', 'hidden');
      });

    });

  };

}(jQuery));