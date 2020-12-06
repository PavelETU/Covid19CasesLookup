# Covid19CasesLookup
App to look up Covid cases from https://covid19api.com/

Uses:
- Jetpack Compose (integration into exististing UI)
- BDD style UI tests
- Unit tests
- MVVM architecture
- StateFlow
- SharedFlow
- Hilt for dependency injection
- View Binding

Work in progress / TODO:
- Add more UI tests:
    - General UI tests (navigation btw screens etc)
    - Tests for Stats screen with the leverage of a new compose UI testing
- Add Room backup
- Add search button to the countries list
- Change colors for recovered cases
- Detect location on the fly instead of checking for the last obtained location

For the last stable version checkout master branch

<img src="screenshot_second_version.png" width="200">
