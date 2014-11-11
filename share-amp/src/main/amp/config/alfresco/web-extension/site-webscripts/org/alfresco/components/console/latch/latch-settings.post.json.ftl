{
   "success": ${success?string}
   <#if !success && statusResponse??>
   ,"status":{
   		<#if statusResponse.status??>
   			"code": "${statusResponse.status.code}",
   			"description": "${statusResponse.status.description}",
   		</#if>
   		<#if statusResponse.message??>
   			"message": "${statusResponse.message}",
   		</#if>
   		<#if statusResponse.exception??>
   			"exception": "${statusResponse.exception}"
   		</#if>
   	}
   </#if>
	
}
