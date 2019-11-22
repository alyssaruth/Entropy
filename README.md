# Entropy
Old entropy project that pre-dated Dartzee. Entirely written in Eclipse, with dependencies managed manually through project setup.

Towards the end I'd started working on an Android app, and in attempting to re-use as much as possible had ended up with a large number of modules:

 - AppUpdater: Small app which was bundled into EntropyUpdater.jar as part of the release. Used to download updates from the server and swap the new JAR file in.
 - Core: Code that was not Entropy-specific or platform-specific, e.g. String or Maths utilities I'd hand-cranked myself.
 - DesktopCore: Non-Entropy-specific code only suitable for Desktop (not reusable by Android) - e.g. Swing stuff.
 - Entropy: Client logic for the Entropy application
 - EntropyAndroid: The (very) modest beginnings of an Android app that never went anywhere. It has a login screen which successfully was able to authenticate, but that's it.
 - EntropyClient: Shared code between EntropyAndroid + Entropy
 - EntropyClientAndServer: Shared code between Entropy + EntropyServer (barely anything, just some initialisation stuff not used in Android by the looks of it)
 - EntropyCore: Logic shared across Entropy, EntropyServer and EntropyAndroid
 - EntropyServer: Server-side logic deployed as a JAR file to an AWS EC2 instance 
 
 
 ## Other Notes
 
 - As well as the unfinished Android project, the main Entropy code itself contains unreleased changes as I was working on various new achievements. 
 - I used Proguard to obfuscate the client JAR, with the map file provided to the server for stack deobfuscation when sending logs
 - The server-side code used to contain credentials for an email account that logs were sent to - these have been redacted
