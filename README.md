# EconoStats

### IMPORTANT! 
### DISCLAIMER: I take NO RESPONSIBILITY for anything that might happen (eg. data loss, corruption of data etc.) to any part of the system and/or software this application is executed upon, or has any communication with (such as Google Drive).
### That being said, it should work fine.

A simple tool for creating spreadsheet reports of bank transactions.
Early alpha version, so don't expect too much.

### HOW TO INSTALL
Right now there is no install. 
Currently you have to compile it and run it from IDE/CLI.
I will address this as soon as time permits...
It's very primitive, but not too hard if you know what you're doing...

0. All file paths and filenames (bank csv file and filters) are configurable in application.properties 
1. Add filters by creating payeeFilters.json (path in application.properties)
```
{
  "payeeFilters": [
    {
      "alias": "ACME Payments",
      "payees": [
        "ACME1 AB 1234-5678",
        "ACME2 AB 8765-4321",
      ],
      "excludePayees": [ACME2 AB REFUNDS]
    }
}
```
2. Add your bank csv file (path in application.properties)
    Note: nordea only right now, unless you add your own implementation...
3. Add Google Drive credentials.json, put it in src/main/resources. You have to do this yourself for now =)
See https://developers.google.com/drive/api/v3/quickstart/java
    Note: This should not be configurable, so for now you need to register the app yourself...

### TODOs
* TODO: Create GUI
* TODO: Add notes to each entry that contains transaction date
* TODO: Use Google Spreadsheet API instead of OdfToolkit (https://developers.google.com/sheets/api/quickstart/java)
* TODO: Fix file encoding problems
* TODO: Add I18N
* TODO: Find a good statistics tool and display some nice stats... start with a pie chart!

