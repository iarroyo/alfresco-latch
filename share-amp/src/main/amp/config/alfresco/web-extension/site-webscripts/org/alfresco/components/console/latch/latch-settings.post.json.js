<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

/**
 * Latch Pair Token
 * 
 * @method POST
 */
 
function main()
{
	
	var appID, secret;
	var enabled=false;
	var success=false;
	var configured=false;
	
	var names = json.names();
	
    for (var i=0; i<names.length(); i++)
    {
    	var field = names.get(i);
	      
	    if (field == "enabled")
	    {
	    	enabled = json.get(field);
	    	enabled= enabled!=null && enabled == "on"?true:false;
	    }
	    else if (field == "appID")
	    {
	    	appID = json.get(field);
	    }
	    else if (field == "secret")
	    {
	    	secret = json.get(field);
	    }
	 }
    
	
	var url="/api/latch/settings";
	
    var params = new Array(3);
	params["enabled"] = enabled;
    params["appID"] = appID;
    params["secret"] = secret;
    

    var response= doPostCall(url,jsonUtils.toJSONString(params));
    
    if(status.code==200 && response){
    	success=response.success;
    	configured=response.configured;
    	if(response.message){
    		model.message=response.message;
    	}
    }else{
    	var statusResponse=JSON.parse(status.message);
		model.statusResponse=statusResponse;
    }
    
    model.success=success;
    model.configured=configured;

    
}

main();