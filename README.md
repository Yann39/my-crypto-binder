# General #

This application allows you to manage your cryptocurrency portfolio.
It is directly connected to major exchanges so you don't have to manually fill the orders.

### Set up ###

* Create and API key in your favorite exchange
* Go to the settings page and add the key corresponding to the exchange
* Click the synchronize button
* Enjoy a beer

### Architecture ###

Java 8
Uses argon2 Java implementation for settings storage : https://github.com/phxql/argon2-jvm
Uses Android architecture components
Uses Room as ORM and singleton for database instance
Uses LiveData/Observer
Uses Paging
Uses MVVM (viewmodels) bindings
Uses Retrofit as HTTP client helper : http://square.github.io/retrofit/
GSON (Retrofit2 + oKHttp3 + Gson2 ?)
Uses MPAndroidChart for charts : https://github.com/PhilJay/MPAndroidChart

### Licence ###

You cannot use this application for profit.
Do not remove any copyright if you clone this repository


Retrofit 2 completely relies on OkHttp for any network operation. The developers of OkHttp have released a separate logging interceptor project, which implements logging for OkHttp. You can add it to your project with a quick edit of your
compile 'com.squareup.okhttp3:logging-interceptor:3.8.0'