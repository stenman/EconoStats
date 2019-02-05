# EconoStats

### IMPORTANT! 
### DISCLAIMER: I take NO RESPONSIBILITY for anything that might happen (eg. data loss, corruption of data etc.) to any part of the system and/or software this application is executed upon or has any communication with (such as Google Drive).
### That being said, it should work fine.

A simple tool for creating spreadsheet reports of bank transactions.
Early alpha version, so don't expect too much.
Also, it's very much a work in progress, so the current snapshot may not even work properly!

### HOW TO INSTALL
Right now there is no install. 
Currently you have to compile it and run it from IDE/CLI.
I will address this as soon as time permits...
It's very primitive, but not too hard if you know what you're doing...

1. Download account transactions from your bank as a csv file.
2. Set the path to aforementioned csv file in application.properties (_.csvFilePath_).
    Note: nordea only right now, unless you add your own implementation...
3. Add Google Drive credentials.json, put it in src/main/resources. You have to do this yourself for now =)
See https://developers.google.com/drive/api/v3/quickstart/java
    Note: This should not be configurable, so for now you need to register the app yourself...

### TODOs
* TODO: Create GUI (work in progress)
* TODO: Add spreadsheet notes to each entry that contains transaction date
* TODO: Use Google Spreadsheet API instead of OdfToolkit (https://developers.google.com/sheets/api/quickstart/java)
* TODO: Fix file encoding problems
* TODO: Add I18N
* TODO: Find a good statistics tool and display some nice stats... start with a pie chart!

