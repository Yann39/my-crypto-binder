## General

The purpose of this application is to allow to easily retrieve trading data from different
online exchanges. It only allows visualization of data, it can **NOT** be used as a trading
platform.

It has been kept very simple, by gathering only trade data needed to calculate the value of your
portfolio and some statistics.

It is directly connected to major exchanges so you don't have to manually fill the orders.

Here is the currently supported exchanges :
- Kraken
- Poloniex
- Bitfinex
- Bittrex

## Usage

1. Create and API key in your favorite exchange platform (read only recommended)
2. In the application, go to the _settings_ page and add the key corresponding to the exchange under the _AppSetting_ menu
3. Still in the settings page, click the 'synchronize' button under the _Synchronize with exchanges_ menu
4. Enjoy a beer watching your million dollars

## Technical details

#### SDK
Minimum SDK : 19 (Android KitKat)\
Targeted SDK : 27 (Android Oreo)

#### Technologies

- Java 8
- SQLite
- [Argon2 Java implementation](https://github.com/phxql/argon2-jvm) for encrypting API keys. This is one of the best key derivation function at the time of writing. There is no need of encrypting API keys, this is just for fun -_-
- [Android architecture components](https://developer.android.com/topic/libraries/architecture/index.html)
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel.html) helper class to facilitate the MVVM architecture
  - [LiveData](https://developer.android.com/topic/libraries/architecture/livedata.html) as data holder, to keep data up to date by respecting application lifecycle
  - [Paging](https://developer.android.com/topic/libraries/architecture/paging.html) library for displaying large sets of data (paged lists)
  - [Room](https://developer.android.com/topic/libraries/architecture/room.html) as persistence library (with singleton database instance)
- [Retrofit](http://square.github.io/retrofit/) as HTTP client helper (GSON2 + OKHttp3)
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) library for charts\

Retrofit 2 completely relies on OkHttp for any network operation. The developers of OkHttp have released a separate logging interceptor project, which implements logging for OkHttp. You can add it to your project with a quick edit of your
compile 'com.squareup.okhttp3:logging-interceptor:3.8.0'

#### Architecture

Application has been implemented using the MVVM (Model-View-ViewModel) pattern as described below :

![Architecture schema](doc/architecture.svg "Architecture")

## Licence

[General Public License (GPL) v3](https://www.gnu.org/licenses/gpl-3.0.en.html)

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
    
You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.

## Contributing

Feel free to fork the project and/or create pull requests.