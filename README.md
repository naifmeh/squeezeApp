# Management android application for SqueezeFace

## Functionalities :

The app allows a number of management actions toward the squeezeFace system.
It makes intensive use of the Golang REST API developed.
The chosen language is Java as we dispose of more Java oriented programmers, allowing easy
maintenance.

The code is made to be the most modular possible, and following the Java and Android specifications.
The volley framework is used for network actions as recommended by the Android guidelines regarding it's
asynchronous and cache oriented aspect.

The app is separated in seven activities :

* SplashActivity : Simply show a charging animations while the app is being setup and the
Java web token is being retrieved.

* MainActivity : Display the different options provided by the app that are to be discused
in the following.

* AddEmployeeActivity : This activity simply adds an employee to the database. The input data in the
authorization fields should follow the dd/mm/yyyy format or an error will make the app crash.

* ViewEmployeesActivity : The activity displays the list of employees in a recycler view. It also allow to update an employee
by clicking the edit button, which will display a bottom sheet fragment as recommended by the material design guidelines.

* TrainNetworkActivity : This activity allows to upload images to the server under a specified folder mentioned in the edit text.
Pictures should only correspond to the person which name has been written as mistakes are not tolerated.
A mistake would either brings errated results or would push the user to delete all the pictures from the database and reupload them.

* ViewLogsActivity : Displays the registered logs on the database in a recycler view.

* SettingsActivity : This settings activity allows to delete the pictures, the logs or update the JWT.


The network actions are performed while the JWT is valid. In case you notice network errors, head to the settings and update
the JWT.


