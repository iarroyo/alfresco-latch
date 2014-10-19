/**
 * ConsoleNodeBrowser tool component.
 * 
 * @namespace Alfresco
 * @class Alfresco.ConsoleLatch
 * @author iarroyo
 */
(function() // Function closure
{
	
   /**
	* Alfresco Slingshot aliases
	*/
	var $html = Alfresco.util.encodeHTML;
	
	Alfresco.ConsoleLatch = function(htmlId) // Component constructor
    {
        /* Give instance a name and call the superclass constructor */
        this.name = "Alfresco.ConsoleLatch";
        Alfresco.ConsoleLatch.superclass.constructor.call(this, htmlId);
        
        /* Register this component */
        Alfresco.util.ComponentManager.register(this);

        /* Load required YUI Components */
        Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "paginator", "json", "history", "animation"], this.onComponentsLoaded, this);

        /* Define panel handlers */
        
        var parent = this; // Cached reference to object since 'this' may refer to something else in handler functions
        
        /* Form Panel Handler */
        FormPanelHandler = function FormPanelHandler_constructor() // 'form' panel constructor
        {
            FormPanelHandler.superclass.constructor.call(this, "form");
        };
        
        YAHOO.extend(FormPanelHandler, Alfresco.ConsolePanelHandler, // Extend Alfresco.ConsolePanelHandler
        {
            /**
             * Called by the ConsolePanelHandler when this panel shall be loaded
             *
             * @method onLoad
             */
            onLoad: function onLoad() // Fired on initial panel load
            {
                
                //Save Button
                parent.widgets.saveBtn = Alfresco.util.createYUIButton(parent, "save-button", null,{
                    type: "submit"
                });
                
                //Cancel Button
                parent.widgets.cancelBtn = Alfresco.util.createYUIButton(parent, "cancel-button", parent.onCancel);
                
                //Edit Button
                parent.widgets.editBtn = Alfresco.util.createYUIButton(parent, "edit-button", parent.onEdit);
                
                //Input and checkbox
                parent.widgets.inputAppID=Dom.get(parent.id+"-appID");
                parent.widgets.inputSecret=Dom.get(parent.id+"-secret");
                parent.widgets.checkboxEnable=Dom.get(parent.id+"-enabled");

                if(parent.options.configured){
                    parent.disableEdit();
                }else{
                    parent.enableEdit();
                }
                
            }
        });
        new FormPanelHandler(); // Instantiate panel instance
        
        // Define any additional form panels here...
        
        return this;
    };
   
    YAHOO.extend(Alfresco.ConsoleLatch, Alfresco.ConsoleTool,
    {

        /**
         * Object container for initialization options
         *
         * @property options
         * @type object
         */
        options: // Console component configurable options
        {
            configured: false
        },

        /**
         * Fired by YUI when parent element is available for scripting.
         * Component initialisation, including instantiation of YUI widgets and event listener binding.
         *
         * @method onReady
         */
        onReady: function ConsoleLatch_onReady() // Fired when component ready
        {
            // Call super-class onReady() method
        	Alfresco.ConsoleLatch.superclass.onReady.call(this);
        	
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
            form.addValidation(this.id + "-appID", Alfresco.forms.validation.mandatory, null, "keyup");
            form.addValidation(this.id + "-secret", Alfresco.forms.validation.mandatory, null, "keyup");
            
            // Initialise the form
            form.init();
            
        },
        
        /**
         * Save Changes form submit success handler
         *
         * @method onSuccess
         * @param response {object} Server response object
         */
        onSuccess: function CL_onSuccess(response)
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
                 this.disableEdit();
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
        onFailure: function CL_onFailure(response)
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
         * Cancel button click handler
         *
         * @method onCancel
         * @param e {object} DomEvent
         * @param p_obj {object} Object passed back from addListener method
         */
        onCancel: function CL_onCancel(e, p_obj)
        {
        	document.location.reload();
        },
        
        /**
         * Edit button click handler
         *
         * @method onCancel
         * @param e {object} DomEvent
         * @param p_obj {object} Object passed back from addListener method
         */
        onEdit: function CL_onEdit(e, p_obj)
        {
        	this.enableEdit();
        },
        
        /**
         * Enable the edition
         * 
         */
        enableEdit: function CL_enableEdit(){
        	
        	//Save Button
        	this.widgets.saveBtn.removeClass("hidden");
        	
        	//Cancel Button
        	this.widgets.cancelBtn.removeClass("hidden");
        	
        	//Edit Button
        	this.widgets.editBtn.addClass("hidden");
        	
        	//Checkbox
        	this.widgets.checkboxEnable.className="";
        	this.widgets.checkboxEnable.removeAttribute("disabled");
        	
        	//AppID
        	this.widgets.inputAppID.className="";
        	this.widgets.inputAppID.removeAttribute("disabled");
        	
        	//Secret
        	this.widgets.inputSecret.className="";
        	this.widgets.inputSecret.removeAttribute("disabled");
        },
        
        /**
         * Disable the edition
         * 
         */
        disableEdit: function CL_disableEdit(){
        	
        	//Save Button
        	this.widgets.saveBtn.addClass("hidden");
        	
        	//Cancel Button
        	this.widgets.cancelBtn.addClass("hidden");
        	
        	//Edit Button
        	this.widgets.editBtn.removeClass("hidden");
        	
        	//Checkbox
        	this.widgets.checkboxEnable.className="disabled";
        	this.widgets.checkboxEnable.setAttribute("disabled","true");
        	
        	//AppID
        	this.widgets.inputAppID.className="disabled";
        	this.widgets.inputAppID.setAttribute("disabled","true");
        	
        	//Secret
        	this.widgets.inputSecret.className="disabled";
        	this.widgets.inputSecret.setAttribute("disabled","true");
        }
 
      
    });

})(); // End function closure