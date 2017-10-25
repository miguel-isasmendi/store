# GCloud local environment setup

Below are the steps to build and run the application.

## Preconditions
1. Java 8 / Jetty 9 runtime or Java 8 runtime
2. Git
3. Maven 3.3.9 or higher
4.  [Cloud SDK](https://cloud.google.com/sdk/docs/)
	1. gcloud components install app-engine-java
	2. [SendgridAccount](https://console.cloud.google.com/launcher/details/sendgrid-app/sendgrid-email)
5. [Lombok](https://projectlombok.org/download)

> **Note: Optionally, you can run gcloud auth application-default login to authorize your user account without performing setup steps. You can  also run gcloud auth activate-service-account --key-file=your_key.json if you want to use a service account. For more information, see Authorizing Cloud SDK Tools.**

## Environment variables & gcloud setup
Commands to run:

* export ENDPOINTS_SERVICE_NAME=<API_NAME>.endpoints.<PROJECT_ID>.cloud.goog
* gcloud config set project <PROJECT_ID>
> You may need to run: **"gcloud auth login"**

## Credentials set up

* gcloud auth login

This command will ask for credentials in order to select an authenticated account to work, and open a browser window to ask for login into a gmail account.

## Useful scripts
* **Running migration scripts**
	* mvn clean package -P<profile> exec:java -DRunMigrationScripts
* **Deploying indexes**
	* gcloud datastore create-indexes <path_to_index.yaml>
* **Creating OpenAPI file**
	* mvn -P<profile> exec:java -DGetSwaggerDoc 
* **Deploying api definition**
	* gcloud service-management deploy openapi.json
 * **Compiling Running application**
	* mvn -P<profile> clear package appengine:run
* **Deploying application**
	* mvn -P<profile> appengine:deploy
	* mvn  -P<profile> appengine:deploy -Dapp.deploy.promote=false

## Code changes required

### Api annotated Classes
Check for every @Api annotated Class to have the following data

    @Api(
    ...
     issuer = "https://securetoken.google.com/<PROJECT_ID>"
    ...
    audiences = {"<PROJECT_ID>" }
    ...

### appengine-web.xml
> **put the version_id in appengine-web.xml
Make sure the tag application looks like this :**

    <application>**<PROJECT_ID>**</application>

### Steps for a happy deploy
1. **Deploying indexes**
2. **Running migration scripts**
3. **Creating OpenAPI file**
4. **Deploying api definition**
	* This will return a string with the format yyyy-mm-ddr[number] and you will need to put that into the appengine-web.xml as follows:

		> <env-var name="ENDPOINTS_SERVICE_VERSION" value="**<yyyy-mm-ddr[number]>**"/  >
	* Also, you will need to check the tag:
		> < application>**<PROJECT_ID>**</ application>

5. **Compiling and Running application**
6. **Deploying application**

> Written with [StackEdit](https://stackedit.io/).
