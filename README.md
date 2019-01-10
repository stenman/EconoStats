# EconoStats

A simple tool for creating spreadsheet reports of bank transactions.
Early alpha version, so don't expect too much.

### HOW TO INSTALL
Right now there is no install. You have to compile it and run it from IDE/CLI.
Very primitive. I will address this asap.
1. Add filters by creating payeeFilters.json, put it in src/main/resources
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
2. Add your bank csv file (nordea only right now), fix path in EconoStats.java (CSV_FILE)
3. Add Google Drive credentials.json, put it in src/main/resources. You have to do this yourself for now =)
See https://developers.google.com/drive/api/v3/quickstart/java

### TODOs
* TODO: Use config file, save in "EconoStats" folder
* TODO: Create GUI
* TODO: Add notes to each entry that contains transaction date
* TODO: Use Google Spreadsheet API instead of OdfToolkit (https://developers.google.com/sheets/api/quickstart/java)
* TODO: Log stuff
* TODO: Fix file encoding problems
* TODO: Add I18N
* TODO: Find a good statistics tool and display some nice stats... start with a pie chart!

