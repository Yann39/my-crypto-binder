# General #

This application allows you to manage your cryptocurrency portfolio.
It is directly connected to major exchanges so you don't have to manually fill the orders.

### Set up ###

* Create and API key in your favorite exchange
* Go to the settings page and add the key corresponding to the exchange
* Click the synchronize button
* Enjoy a beer

### Architecture ###

Uses argon2 Java implementation for settings storage : https://github.com/phxql/argon2-jvm
Uses Android architecture components
Uses Room as ORM and singleton for database instance
Uses LiveData/Observer
Uses MVVM (viewmodels) bindings
Uses Retrofit as HTTP client helper : http://square.github.io/retrofit/
Uses MPAndroidChart for charts : https://github.com/PhilJay/MPAndroidChart

### Licence ###

You cannot use this application for profit.
Do not remove any copyright if you clone this repository