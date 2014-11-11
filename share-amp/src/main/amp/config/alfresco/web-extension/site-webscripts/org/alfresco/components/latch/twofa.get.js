/**
 * Latch Token component controller GET method
 */
function main()
{
   model.loginUrl = url.context + "/page/dologin";

   var username=page.url.args['username'];
   var isTwoFA=false;

   if(username!=undefined){
   		var response = remote.call("/api/latch/twofa/" + encodeURIComponent(username));
   		if (response.status == 200)
   		{
   			var json = JSON.parse(response);
   			isTwoFA=json.success;
   		}
	}

   var login = {
      id: "Login",
      name: "Alfresco.component.Login"
   };
   
   model.widgets = [login];
   model.isTwoFA=isTwoFA;

}

main();