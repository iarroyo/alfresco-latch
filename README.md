#LATCH INSTALLATION GUIDE FOR ALFRESCO

This is a beta latch plugin that implements two functionalities for Alfresco Share:

* Latch
* Two Factor Authentication


##PREREQUISITES 
 * Alfresco 4.2 or higher
    * Tested with Alfresco Community version [5.0.a]

 * To get the **"Application ID"** and **"Secret"**, (fundamental values for integrating Latch in any application), it’s necessary to register a developer account in [Latch's website](https://latch.elevenpaths.com). On the upper right side, click on **"Developer area"**.
 
 
 
##LATCH DEVELOPER AREA
 * When the account is activated, the user will be able to create applications with Latch and access to developer documentation, including existing SDKs and plugins. The user has to access again to [Developer area](https://latch.elevenpaths.com/www/developerArea"https://latch.elevenpaths.com/www/developerArea"), and browse his applications from **"My applications"** section in the side menu.

* When creating an application, two fundamental fields are shown: **"Application ID"** and **"Secret"**, keep these for later use. There are some additional parameters to be chosen, as the application icon (that will be shown in Latch) and whether the application will support OTP  (One Time Password) or not.

* From the side menu in developers area, the user can access the **"Documentation & SDKs"** section. Inside it, there is a **"SDKs and Plugins"** menu. Links to different SDKs in different programming languages and plugins developed so far, are shown.


##BUILD LATCH MODULE FOR ALFRESCO
This proyect is based on Alfresco Maven SDK so to build the project you only need to follow these steps:

* Go to main folder /alfresco-latch, execute the mvn clean package.

* After the package we will have the next output:
    * /alfresco-latch/repo-amp/target/latch-repo.amp, which is the amp module for alfresco repository
    * /alfresco-latch/share-amp/target/latch-share.amp, which is the amp module for alfresco share
    


###WARNING

This project use the **latch java SDK** as dependency so you cannot build the project correctly if you don´t include this dependency inside your .m2 repository.
To do it, download the latch-java-sdk project (https://github.com/ElevenPaths/latch-sdk-java) and install it by mvn install.


##GUIDE TO INSTALL LATCH MODULE FOR ALFRESCO

This guide try to explain how install the latch customizations in Alfresco.

**1.** You can download the amps from the "**Downloads**" section at the end of the page or build them following the instruction of the section "**Build Latch Module for Alfresco**"

**2.** Place the amps in their respective directories

   * Latch Alfresco Repository AMP (latch-repo.amp) ---> &lt;alfresco_root_directory&gt;/amps 
   * Latch Alfresco Share AMP (latch-share.amp) ---> &lt;alfresco_root_directory&gt;/amps_share

**3.** There are two options to install the amps:

**3.1** The script called **apply_amps.sh**.

Install all AMP files that are located in the amps and amps_share directories.
And remove the existing previous deployed wars.
    
**3.2** The jar **alfresco-mmt.jar** 
         
**Example**: java -jar  &lt;alfresco_root_directory&gt;/bin/alfresco-mmt.jar install &lt;path_amp&gt; &lt;path_war&gt; -verbose
   
- Install the amp to Alfresco:  
          java -jar &lt;alfresco_root_directory&gt;/bin/alfresco-mmt.jar install &lt;alfresco_root_directory&gt;/amps/latch-repo.amp &lt;alfresco_root_directory&gt;/tomcat/webapps/alfresco.war -verbose

- Install the amp to Share:  
java -jar &lt;alfresco_root_directory&gt;/bin/alfresco-mmt.jar install &lt;alfresco_root_directory&gt;/amps_share/latch-share.amp &lt;alfresco_root_directory&gt;/tomcat/webapps/share.war -verbose
 
In this case you need to remove the folders alfresco and share placed in **&lt;alfresco_root_directory&gt;/tomcat/webapps**

**4.** After the installation check that really the alfresco and share folders have been removed in the case of using the script (apply_amps.sh) 

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
	
##ADITIONAL INFORMATION
The latch website use a certified issued by **StartCom Ltd.**. This certificate is not included in the java keystore bundle by default, so we have two options to validate the certified correctly. The second option is used by default.

1. Import it in our cacerts trustore.  
**keytool -import -trustcacerts -file /path/to/ca/ca.pem -alias CA_ALIAS -keystore $JAVA_HOME/jre/lib/security/cacerts**

2. Use a custom keystore already containing the needed certificate in order to be used by the http client. A Java keystore only containing the Latch certificate is already provided with the Alfresco Latch plugin.


##DOWNLOADS
* [Latch Alfresco Repository AMP](https://mega.co.nz/#!VFgC2C5Y!EDYysD-Gq_Y4Up-DtYGCBzrxRSu-FgfVwUGUgOeYI0A)
* [Latch Alfresco Share AMP](https://mega.co.nz/#!sFpzmTQL!c2yFKjLtW305QY-HuhhD60n8kugNEnsOhe8WbelZ0Wk) 
