# EconoStats

### IMPORTANT! 
### DISCLAIMER: I take NO RESPONSIBILITY for anything that might happen (eg. data loss, corruption of data etc.) to any part of the system and/or software this application is executed upon or has any communication with (such as Google Drive).
### That being said, it should work fine. <<< The Google Drive API commands used can never overwrite or delete something that is not created by the app itself >>>

A simple tool for creating spreadsheet reports of bank transactions.
Early alpha version, so don't expect too much.
Also, it's very much a work in progress, so the current snapshot may not even work properly!

### HOW TO INSTALL
Right now there is no install. 
Currently you have to compile it and run it from IDE/CLI.
I will address this as soon as time permits...

1. Add Google Drive credentials.json, put it in src/main/resources. You have to do this yourself for now =)
See https://developers.google.com/drive/api/v3/quickstart/java
    - Click "Enable the Drive API"
    - Download credentials
    - Put credentials.json in src/main/resources
    Note: This should not be configurable, so for now you need to register the app yourself...
2. Download account transactions from your bank as a csv file
3. Run the application
4. In the text field, enter the path to the csv (or find it by clicking the _Open..._ button)
    Note: nordea only right now, unless you add your own implementation...
5. Click _Load From Disk_
6. Click the "Payee Filters" tab and create some filters
7. Go back to the "Econo Stats" tab and now click "Generate Recurring Transactions"
8. The generated spreadsheet should now be in a folder called "EconoStats" in the root of your Google Drive

### TODOs
* TODO: Add spreadsheet notes to each entry that contains transaction date
* TODO: Use Google Spreadsheet API instead of OdfToolkit (https://developers.google.com/sheets/api/quickstart/java)
* TODO: Fix file encoding problems
* TODO: Add I18N
* TODO: Find a good statistics tool and display some nice stats... start with a pie chart!

