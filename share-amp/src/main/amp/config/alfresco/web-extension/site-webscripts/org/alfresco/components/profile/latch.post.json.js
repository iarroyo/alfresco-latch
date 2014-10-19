<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

/**
 * Latch Pair Token
 * 
 * @method POST
 */
 
function main()
{
	
	var token=null;
	var operation=null;
	var success=false;
	
	var names = json.names();
	
    for (var i=0; i<names.length(); i++)
    {
    	var field = names.get(i);
	      
	    // look and set simple text input values
	    if (field == "token")
	    {
	    	token = json.get(field);
	    }
	    else if (field == "operation")
	    {
	    	operation = json.get(field);
	    }
	 }
    
	
	var url="/api/latch/"+encodeURIComponent(operation);
	
    var params = new Array(2);
    params["username"] = user.name;
    params["token"] = token;
    

    var response= doPostCall(url,jsonUtils.toJSONString(params));
    model.operation=operation;
    if(response && response.success){
    	success=response.success;
    }else{
       var statusResponse=JSON.parse(status.message);
       model.statusResponse=statusResponse;
       
    }
    model.success=success;

    
}

main();