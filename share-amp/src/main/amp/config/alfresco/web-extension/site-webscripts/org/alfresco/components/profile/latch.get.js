<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

function main()
{
	var url="/api/people/"+encodeURIComponent(user.name);
    var userJSON= doGetCall(url);
    
	url="/api/latch/settings";
    var settings= doGetCall(url);
    
	model.currentUser=userJSON;
	model.settings=settings;
}

main();

