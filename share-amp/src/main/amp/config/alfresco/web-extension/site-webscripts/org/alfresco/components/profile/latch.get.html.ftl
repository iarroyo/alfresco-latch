<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/profile/profile.css" group="profile"/>
   <@link href="${url.context}/res/components/profile/latch.css" group="profile"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/profile/latch.js" group="profile"/>
</@>

<@markup id="widgets">
   <@createWidgets group="profile"/>
</@>

<#assign isPaired=currentUser.latchAccountID?? />

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <script type="text/javascript">//<![CDATA[
	    new Alfresco.Latch("${el}").setOptions(
	   {
	      buttonID: <#if !isPaired>"button-pair"<#else>"button-unpair"</#if>
	   });
	  //]]></script>
      <div id="${el}-body" class="profile latch">
      
      	 <#if user.name==page.url.templateArgs.userid>
	      	 <#if settings.enabled && settings.configured>
		         <form id="${el}-form" action="${url.context}/service/components/profile/latch" method="post">
		            <div class="header-bar">${msg("label.latch")}</div>
			            <div class="row">
			            	<#if !isPaired>
			               		<span class="latch-label"><label for="${el}-token">${msg("label.pair.token")}:</label></span>
			               		<span><input type="text" name="token" maxlength="10" size="30" id="${el}-token" /></span>
			           		<#else>
			        			<span class="latch-label"><label for="${el}-paired">${msg("label.paired")}.</label></span>
			        		</#if>
			            </div>
		            <hr/>
		            <div class="buttons">
		               <#if !isPaired>	
		               		<button id="${el}-button-pair" name="pair">${msg("button.pair")}</button>
		               <#else>
		               		<button id="${el}-button-unpair" name="unpair">${msg("button.unpair")}</button>
		               </#if>
		            </div>
		            <input type="hidden" name="operation" value="<#if !isPaired>pair<#else>unpair</#if>"/>
		         </form>
		     <#else>
				<div class="header-bar">${msg("label.latch")}</div>
				<div class="row">
					<span class="latch-label"><label>${msg("label.latch.unavailable")}.</label></span>
				</div>
		     </#if>
		 <#else>
			<div class="header-bar">${msg("label.latch")}</div>
			<div class="row">
				<span class="latch-label"><label>${msg("label.latch.accessDenied")}.</label></span>
			</div>
	     </#if> 
      </div>
   </@>
</@>

