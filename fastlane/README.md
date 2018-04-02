fastlane documentation
================
# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```
xcode-select --install
```

Install _fastlane_ using
```
[sudo] gem install fastlane -NV
```
or alternatively using `brew cask install fastlane`

# Available Actions
## Android
### android deploy
```
fastlane android deploy
```
Deploy a new version to Google Play
### android bump_major
```
fastlane android bump_major
```
Bump the major version of the application
### android bump_minor
```
fastlane android bump_minor
```
Bump the minor version of the application
### android bump_patch
```
fastlane android bump_patch
```
Bump the patch version of the application

----

This README.md is auto-generated and will be re-generated every time [fastlane](https://fastlane.tools) is run.
More information about fastlane can be found on [fastlane.tools](https://fastlane.tools).
The documentation of fastlane can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
