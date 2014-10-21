<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/console/latch-settings.css" group="console"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/console/consoletool.js" group="console"/>
   <@script src="${url.context}/res/components/console/latch-settings.js" group="console"/>
</@>

<@markup id="widgets">
   <@createWidgets group="console"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <!--[if IE]>
      <iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe> 
      <![endif]-->
      <input id="yui-history-field" type="hidden" />
	  
	  <#assign el=args.htmlid?html/>
      <#if settings.configured??>
      	<#assign isConfigured=settings.configured/>
      <#else>
      	<#assign isConfigured=false/>
      </#if>
	  
	  
	<script type="text/javascript">//<![CDATA[
	    new Alfresco.ConsoleLatch("${el}").setOptions(
   	 	{
      		configured: <#if settings.configured??>${settings.configured?string}<#else>false</#if>
   		}).setMessages(${messages});
	//]]></script>
	</script>
      
      <div id="${el}-body" class="latch-settings">
      		<div class="title">${msg("label.settings")}</div>
      		<div class="row info">${msg("label.settings.info")}.<a href="https://latch.elevenpaths.com/www/getting.html" target="_blank">${msg("label.settings")}</a></div>
      	    <form id="${el}-form" action="${url.context}/service/components/console/latch" method="post">
               <div class="row">
                  <div class="label">${msg("label.active")}:</div>
                  <div>
				  	<input type="checkbox" <#if isConfigured>class="disabled" disabled</#if> id="${el}-enabled" name="enabled" title="${msg("label.enabled")}" <#if settings.enabled??>checked="${settings.enabled?string}"</#if> />
                  </div>
               </div>
               <div class="row">
                  <div class="label">${msg("label.appID")}:</div>
                  <div>
                     <input type="text" <#if isConfigured>class="disabled" disabled</#if> id="${el}-appID" name="appID" title="${msg("label.appID")}" value="<#if settings.appID??>${settings.appID}</#if>"/>
                  </div>
               </div>
               <div class="row">
                  <div class="label">${msg("label.secret")}:</div>
                  <div>
                     <input type="text" <#if isConfigured>class="disabled" disabled</#if> id="${el}-secret" name="secret" title="${msg("label.secret")}" value="<#if settings.secret??>${settings.secret}</#if>"/>
                  </div>
               </div>
               <div class="apply">
                  	<button id="${el}-save-button" name="save" <#if isConfigured>class="hidden"</#if>>${msg("button.save")}</button>
                  	<button id="${el}-cancel-button" name="cancel" <#if isConfigured>class="hidden"</#if>>${msg("button.cancel")}</button>
                  	<button id="${el}-edit-button" name="edit" <#if !isConfigured>class="hidden"</#if>>${msg("button.edit")}</button>
               </div>
            </form>
      	
      </div>
   </@>
</@>

