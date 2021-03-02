# Covid19CasesLookup
App to look up Covid cases from https://covid19api.com/

Uses:
- Jetpack Compose (integration into existing UI)
- BDD style UI tests:
    - Example (see more in CasesLookupUITest): 
    ```kotlin
    @Test
    fun iCanSearchListOfCountries() = bddTestCase {
        givenIOpenTheApp()
        andIFillTheSearchWith("un")
        iSeeOnlyFollowingCountries(listOf("United Kingdom"))
    }
  ```
- Compose UI tests
- Espresso UI tests
- Unit tests
- MVVM architecture
- Coroutines
- Flow (StateFlow, SharedFlow)
- Hilt for dependency injection
- View Binding

Work in progress / TODO:
- Add more UI tests:
    - Check stats
    - Check stats changing after changing months/types of cases
- Add Room backup
- Change colors for recovered cases
- Detect location on the fly instead of checking for the last obtained location

For the last stable version checkout master branch

<img src="media/cases_lookup.gif">
