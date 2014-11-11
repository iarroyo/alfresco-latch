
/**
 * Latch component.
 * 
 * @namespace Alfresco
 * @class Alfresco.Latch
 * @author iarroyo
 * 
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;
      
   /**
    * Latch constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.Latch}
    * @constructor
    */
   Alfresco.Latch = function(htmlId)
   {
      Alfresco.Latch.superclass.constructor.call(this, "Alfresco.Latch", htmlId, ["button"]);
      return this;
   }
   
   YAHOO.extend(Alfresco.Latch, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
    	  buttonID: "button-pair"
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function UP_onReady()
      {
         // Reference to self used by inline functions
         var me = this;
         
         // Buttons
         this.widgets.button = Alfresco.util.createYUIButton(this, this.options.buttonID, null,
            {
               type: "submit"
            });
         
         
         // Form definition
         var form = new Alfresco.forms.Form(this.id + "-form");
         form.setSubmitElements(this.widgets.button);
         form.setSubmitAsJSON(true);
         form.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onSuccess,
               scope: this
            },
         
         	failureCallback:
         	{
         		fn: this.onFailure,
         		scope: this
         	}
         });
         
         // Form field validation
         form.addValidation(this.id + "-token", Alfresco.forms.validation.mandatory, null, "keyup");
         //TODO add lenth min, max validation
         
         // Initialise the form
         form.init();
         
         // Finally show the main component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "display", "block");
      },

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */
      
      /**
       * Save Changes form submit success handler
       *
       * @method onSuccess
       * @param response {object} Server response object
       */
      onSuccess: function UP_onSuccess(response)
      {
         if (response && response.json)
         {
            if (response.json.success)
            {
               // succesfully updated details - refresh back to the user profile main page
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: Alfresco.util.message("message.success", this.name)
               });
               this.navigateToLatch();
            }
            else if (response.json.message)
            {
               Alfresco.util.PopupManager.displayPrompt(
               {
                  text: response.json.message
               });
            }
         }
         else
         {
            Alfresco.util.PopupManager.displayPrompt(
            {
               text: Alfresco.util.message("message.failure", this.name)
            });
         }
      },
      
      /**
       * Failure handler
       *
       * @method onFailure
       * @param response {object} Server response object
       */
      onFailure: function UP_onFailure(response)
      {
    	  var message= "message.failure";
    	  if(response.json && response.json.status && response.json.status.message){
    		  message=response.json.status.message;
    	  }
    	  Alfresco.util.PopupManager.displayPrompt(
    	  {
    	  	text: Alfresco.util.message(message)
          });
      },
      
      /**
       * Perform URL navigation back to user latch page
       * 
       * @method navigateToLatch
       */
      navigateToLatch: function navigateToLatch()
      {
         var pageIndex = document.location.href.lastIndexOf('/');
         document.location.href = document.location.href.substring(0, pageIndex + 1) + "latch";
      }
   });
})();