# Twitter-RHO-Android_Challenge

This project consists on a challenge to a possible job.
Its objectives are:

- Get Twitter public feed using its streaming API;
- Search for a word provided by the user;
- Every tweet has a lifespan, meaning that after that time, they will have to be removed from the list;
- Once collected, the tweets will be included in a view;
- If the connection is interrupted or the app is run offline, the tweets that  were alive during the previous execution will be shown until a connection is established.

### Getting Started

Twitter developer account is needed. An example of how to do it:
https://www.extly.com/docs/autotweetng_joocial/tutorials/how-to-auto-post-from-joomla-to-twitter/apply-for-a-twitter-developer-account/#apply-for-a-developer-account
Finally, change the API keys in network/Utils.kt file to yours.

## This app uses Android Jetpack components like:
- MVVM Architecture
- LiveData
- Kotlin Coroutines
- Navigation Component
- Data Binding

## Other libraries used
- Retrofit
- OkHttp
- Gson

## Authors

* **Miguel Maia**
