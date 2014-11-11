<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

function main()
{
	var url="/api/latch/settings";
    var settings= doGetCall(url);
	model.settings=settings;
}

main();

