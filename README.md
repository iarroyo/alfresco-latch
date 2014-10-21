#LATCH INSTALLATION GUIDE FOR ALFRESCO

This is a beta latch plugin for Alfresco.


##PREREQUISITES 
 * Alfresco 
 	* Tested with Alfresco Community version [5.0.a]
	* Tested with Alfresco Enterprise version [4.2.0.3, 4.2.3.3]

 * To get the **"Application ID"** and **"Secret"**, (fundamental values for integrating Latch in any application), it’s necessary to register a developer account in [Latch's website](https://latch.elevenpaths.com). On the upper right side, click on **"Developer area"**.
 
 
 
##LATCH DEVELOPER AREA
 * When the account is activated, the user will be able to create applications with Latch and access to developer documentation, including existing SDKs and plugins. The user has to access again to [Developer area](https://latch.elevenpaths.com/www/developerArea"https://latch.elevenpaths.com/www/developerArea"), and browse his applications from **"My applications"** section in the side menu.

* When creating an application, two fundamental fields are shown: **"Application ID"** and **"Secret"**, keep these for later use. There are some additional parameters to be chosen, as the application icon (that will be shown in Latch) and whether the application will support OTP  (One Time Password) or not.

* From the side menu in developers area, the user can access the **"Documentation & SDKs"** section. Inside it, there is a **"SDKs and Plugins"** menu. Links to different SDKs in different programming languages and plugins developed so far, are shown.


##BUILD AND INSTALL LATCH MODULE FOR ALFRESCO
This proyect is based on Alfresco Maven SDK so to build the project you only need to follow these steps:

* Go to main folder /alfresco-latch, execute the mvn clean package.

* After the package we will have the next output:
	* /alfresco-latch/repo/target/repo.war, which is the alfresco.war
	* /alfresco-latch/share/target/share.war
	* /alfresco-latch/repo-amp/target/latch-repo.amp, which is the amp module for alfresco repository
	* /alfresco-latch/share-amp/target/latch-share.amp, which is the amp module for alfresco share
	
* You can use both wars directly or install the latch-repo.amp and latch-share.amp separately.

##WARNING
This project use the **latch java SDK** as dependency so you cannot build the project correctly if you don´t include this dependency inside your .m2 repository.
To do it, download the latch-java-sdk project (https://github.com/ElevenPaths/latch-sdk-java) and install it by mvn install.




##USE OF LATCH MODULE FOR THE USERS
**Latch does not affect in any case or in any way the usual operations with an account. It just allows or denies actions over it, acting as an independent extra layer of security that, once removed or without effect, will have no effect over the accounts, that will remain with its original state.**

###Latch administration in Alfresco
To configure the latch application in Alfresco you should include the application ID and the secret. This operation is only available by administrator.
Go to Admin Console and you should find the Latch Settings where you should add the application ID and the secret provided by your latch application.

###Pairing a user in Alfresco
The user needs the Latch application installed on the phone, and follow these steps:

* **Step 1:** Logged in your own account, go to **"My Profile"**, and click on the **"Latch"** tab.

* **Step 2:** From the Latch app on the phone, the user has to generate the token, pressing on **“Add a new service"** at the bottom of the application, and pressing **"Generate new code"** will take the user to a new screen where the pairing code will be displayed.

* **Step 3:** The user has to type the characters generated on the phone into the text box displayed on the web page. Click on **"Pair Account"** button.

* **Step 4:** Now the user may lock and unlock the account, preventing any unauthorized access.


###Unpairing a user in Alfresco
* From your account go to the link **“My profile”**, go to the **“Latch"** tab and click the button **“Unpair Account”**. Finally, an alert indicating that the service has been unpaired will be displayed.


##RESOURCES
- You can access Latch´s use and installation manuals, together with a list of all available plugins here: [https://latch.elevenpaths.com/www/developers/resources](https://latch.elevenpaths.com/www/developers/resources)

- Further information on de Latch´s API can be found here: [https://latch.elevenpaths.com/www/developers/doc_api](https://latch.elevenpaths.com/www/developers/doc_api)

- For more information about how to use Latch and testing more free features, please refer to the user guide in Spanish and English:
	1. [English version](https://latch.elevenpaths.com/www/public/documents/howToUseLatchNevele_EN.pdf)
	1. [Spanish version](https://latch.elevenpaths.com/www/public/documents/howToUseLatchNevele_ES.pdf)
	

##DOWNLOADS
* [Latch Alfresco Repository AMP](https://mega.co.nz/#!RRIh2QpJ!4Ko2q9nEh4AqGqfDTCXWosj8M8WwsHwdzC0jX3ePGkY)
* [Latch Alfresco Share AMP](https://mega.co.nz/#!Ac5D2K4C!Y3WOveYMHUsqnuSt_ykeXM9_XNGvtjSyDGp6J0gxOwk) 
