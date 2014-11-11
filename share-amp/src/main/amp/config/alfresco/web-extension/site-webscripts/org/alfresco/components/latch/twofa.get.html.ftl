<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/guest/login.css" group="login"/>
   <@link href="${url.context}/res/components/latch/twofa.css" group="login"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/guest/login.js" group="login"/>
</@>

<@markup id="widgets">
   <@createWidgets group="login"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <div id="${el}-body" class="theme-overlay login hidden">
      
      <@markup id="header">
         <div class="latch-logo"></div>
      </@markup>

      <#if !isTwoFA>
         <script type="text/javascript">//<![CDATA[
            document.location.href=document.location.href.substring(0,document.location.href.lastIndexOf("/"));
         //]]></script>
      </#if>
      
      <@markup id="form">
         <form id="${el}-form" accept-charset="UTF-8" method="post" action="${loginUrl}" class="form-fields latch">
            <@markup id="fields">
            <div class="form-field">
               <label for="${el}-token">${msg("label.token")}</label><br/>
               <input type="text" id="${el}-token" name="token" maxlength="255" value="" />
            </div>
            </@markup>
            <@markup id="buttons">
            <div class="form-field">
               <input type="submit" id="${el}-submit" class="login-button" value="${msg("button.accept")}"/>
            </div>
            </@markup>
         </form>
      </@markup>
      
      <@markup id="footer">
         <div class="copy">${msg("label.copyright")}</div>
      </@markup>

      </div>
   </@>
</@>