coinwallet [![Build Status](https://travis-ci.org/cwallet/coinwallet.png?branch=master)](https://travis-ci.org/cwallet/coinwallet)
==========

A bitcoin wallet for Android

In order to use Maven with Eclipse, you'll need m2e-android plugin for Eclipse.


## Folder Structure ##
To remove incorrect folder/package errors, remove the default source directory, and add the following folders as source folders:
 - CoinWallet/src/main/java
 - CoinWallet-tests/src/main/java
 - CoinWallet-tests/src/test/java

## Automatic Builds ##
Automatic builds from Travis-ci can be found in the [results branch](https://github.com/cwallet/coinwallet/tree/results/ "Automatic Builds") sorted by branch, then checksum of the commit, then target API.

## Documentation ##
_To-Do..._

## Development Notes ##
 - Should builds start erroring out randomly on travis, particulary around the generation of sources, check for a new version of the sdk.
