Crowdsourcing helps many people to benefit from the data available freely on the media and forming a prediction of it in such cases as an epidemic or a colossal event happening. In this research we formulate methods of such crowdsourcing activity and by analyzing the patterns of it, predicting the event of such incidents. Data has been fed in to the system and then by training a prediction algorithm, we can give a prediction for current situations of an epidemic. Our main area of research is how crowdsourcing data for Dengue and Influenza which is fed into the system is affected by weather.

According to research, temperature is the most important abiotic environmental factors that can affect the biological process of mosquitoes [1]. Research also suggests that humidity also plays a huge role in dengue mosquito fertility and breeding. Another research suggests that at 25 ºC and 80% relative humidity, females survived two-fold more and produced 40% more eggs when compared to those kept at 35 ºC and 80% relative humidity” [2]. Public health researchers Simon Hales and colleagues published a study in the medical journal The Lancet in 2002, in which they examined links between Dengue virus risk and climate change. They found that 89 percent of the distribution of Dengue fever cases was predicted by vapor pressure, which is a measure of humidity [3].
So these environmental factors suggest that there is a strong correlation of such environmental factors to the infection and epidemic cases. 
Our research focuses on how we can predict the number of dengue cases in each district given current weather details. 


Mobile Application Model
The mobile application is based on Java and the platform is based on Android OS. The application would use two APIs. 1) The weather data given longitude and latitude. 2) The current district, given longitude and latitude. The user information would be stored in a cloud database. We used Amazon DynamoDB, hence any user can access anywhere and it’s really easy to use and setup. Once the user enters a place, the application would get the location data, which is the latitude and longitude of the user using GPS which is built in to the phone. Once obtained it will call the APIs to get the current weather and the district based on the location. The next job would be to predict the percentage of a user to get affected by dengue or an influenza like illness.
Data Analysis
To analyze the relationship between weather and dengue cases, there are several types of data mining methods to choose from, such as clustering, classification and rule mining. We have to choose what fits best and handle the data accordingly to feed to the training algorithm.
Since this is supervised learning, we have to use classification as an approach to predict the outcome.

There are several classifier methods in data mining. Such classifiers include (lazy classifier) IBK, K*, LWL and many others. For classification ensembles there is Bagging and Boosting method. Boosting is taken out of the scope since it is suited with nominal class data. Clustering would not help in our case since the data is for homogenous analysis and clustering. For our prediction of infection rate, this method would not suffice.
We chose WEKA [†] as the data mining tool hence it is open sourced and the libraries are freely available to develop.
In our research approach we would be using Lazy classifier Algorithms and choose which one best fits the data using the least mean square errors. Given below is a description of Lazy classifier method and its basic algorithms that we would be using to classify our data.



Running the UserPreference OM Sample
============================================
This sample demonstrates how Android can interact with Amazon DynamoDB to store user preferences. For a detailed description of the code, open and read the UserPreference.html file.

1. Import the project into Eclipse 
   * Go to File -> Import.  Import Wizard will open.
   * Select General -> Existing Projects into Workspace.  Click Next.
   * In Select root directory, browse to samples directory.  List of all samples projects will appear.
   * Select the projects you want to import
   * Click Finish.

2. Update your App configuration:
   * Make sure you have an identity pool created and configured at https://console.aws.amazon.com/cognito/ and you downloaded the starter code at the last step of the wizard.
   * Update the ACCOUNT_ID, IDENTITY_POOL_ID, TEST_TABLE_NAME, and UNAUTH_ROLE_ID fields in
Constants.java which you can find in src/com/amazonaws/demo/userpreferencesom
   * Note that you do not need to create this table online. The app will create the table, so enter any name.
   * You will need to update the permissions for the role you will use here. 
      * Go to [IAM](https://console.aws.amazon.com/iam/home), select the region in which your role was created, and select roles.
      * Select the appropriate role, then under the permissions tab, select attach role policy, and select Amazon DynamoDB Full Access.

For information on setting up Amazon Cognito for authentication please visit our [Getting started guide](http://docs.aws.amazon.com/mobile/sdkforandroid/developerguide/cognito-auth.html).

