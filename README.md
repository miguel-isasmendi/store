# Project  basic documentation
This documentation is intended to help developers to work with this application.

## Design overview
The whole application was designed to deal with the functionality required for a basic e-commerce like application, having as core of it's behavior the entities that model the Orders, Skus, Products, and Bundles. The explanation of their intent in terms of design is explained in further sections.

As a  quite clear and almost innocent remark I will point out that the system was built thinking in start the backend as a monolithic environment, but with the intent of being split into microservices in a not so distant future. For that purpose, it's clear that the  effort is mostly put in the separation of concerns of the objects involved in te layers of the application and the granularity of the collaborators on the architecture across the Resources-Services-DAO layers we've built. And also should take into account the possibility of make deep use of memcache.

### Orders
The orders will basically hold the data and be affected during all the processes that leads to resolve the transaction that will represent a purchase of some sort of service or good the store is offering.

The design should have the flexibility to allow:
* Several items per Order
    * Each item should reference as an Sku as a recipient of the accountability and specific data of a Product.
* An Order should know it's payments
* It should have Delivery information
* It should allow to have discounts.
* It should store redudant data to avoid the iteration over inner items to calculate totals and exitence of relationships among domain entities.
### Sku
The Skus are the entities storing the accountable data and the specifications configured as part of the possibilities the store wants to offer for a product.

* It holds the price and the typification required to determine the potential value of a order
* all object for which is required some data related to accountability processes, have SKUs asociated, including the Bundle
### Product
It holds the basis, non accountability related data of an entity prone to be offered by the store.
### Bundle
A bundle is an entity that is backed by a SKU that will hold the overall price of the bundle along with other particularity we may find.
* The bundle can have several items each one of them that should reference a SKU (those allowing to nest bundles)
* the price of a bundle should be calculated according to the sum of all it's item's SKUs and their billing type.

### Example
A store could have a Product that's called back "Bike"
The Bike can have four Skus:
1. BillingType=HOUR, price=5
2. BillingType=DAILY, price=20
3. BillingType=WEEKLY, price=60
4. BillingType=CHILD_SPECIFIC, price=n/a

The store has one Bundle called "FamilyRental":
* Root sku=4
* Items
    1. sku=2
    2. sku=3

Also, the application can receive an Order with the following configuration:
* Discount 30%
* Items
    * sku=2
    * sku=4

## Todo list
1. ~~Implement a set of classes to model this domain and logic~~
2. Add automated tests to ensure a coverage over 85%
3. ~~Use GitHub to store and version your code~~
4. ~~Apply all the recommended practices you would use in a real project~~
5. ~~Add a README.md file to the root of your repository to explain: your design, the development practices you applied and how run the tests.~~

## Deliverables
The link of the repository is:
https://github.com/miguel-isasmendi/store

## Build
The project is configured to run on GCloud App Engine, so we have prepared the whole build to run as follows
### Prerequisites
The system should have a properties file **stored in /config/env** that should be named env.properties and should have the following values:

firebase.api.key=<PUT_HERE_THE_API_KEY>

sendgrid.api.key=<PUT_HERE_THE_API_KEY>

1. Java 8 / Jetty 9 runtime or Java 8 runtime
2. Git
3. Maven 3.3.9 or higher
4.  [Cloud SDK](https://cloud.google.com/sdk/docs/)
	1. gcloud components install app-engine-java
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
	* mvn -P<profile> exec:java -DBuildOpenApi 
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
