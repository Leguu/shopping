# Ye Shopping App

For SOEN 387.

**Please note: built-in admin username is `admin`, password `secret`**

## Compilation instructions

1) Ensure you have Gradle installed (https://gradle.org/)
   1) It's available in most package managers like chocolatey and homebrew
2) Run `gradle war` in the root directory
   1) Gradle will automatically install the dependencies found in `build.gradle.ts`
3) If successful, the built war file will be found under `build/libs/shopping.war`

**Please note: the application expects that it is deployed in the root of the URL, and many links in the frontend are absolute. If you deploy the application nested, the links will be broken.**

### If unsuccessful:

Try and use the pre-built war file. If that still doesn't work, you may judge a deployed version at https://shopping.legu.dev

I pinky-promise the deployed version is identical to the submitted version. Verify that it is my app by looking for the commit ID in the footer - it should match the head commit ID in the submitted folder that you can find with `git log`. 
