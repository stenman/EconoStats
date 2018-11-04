package se.perfektum.econostats;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import se.perfektum.econostats.spreadsheet.OdfToolkitSpreadsheetManager;
import se.perfektum.econostats.spreadsheet.SpreadsheetManager;

public class EconostatsMain {
    // Done Tested
    // [x]  [x] read CSV
    // [x]  [x] create object for each transaction in CSV, put each object in List<accountTransaction>
    // [ ]  [ ] find existing "EconoStats" folder (recursively, but this can be done later)
    // [ ]  [ ] create "EconoStats" folder (if "EconoStats" folder did not exist)
    // [ ]  [ ] download existing JSON from "EconoStats" folder (if exists)
    // [ ]  [ ] convert existing JSON into another List<AccountTransaction>
    // [ ]  [ ] compare new and old List<AccountTransaction>:
    // [ ]  [ ]      merge the two lists (insert (into a "full" list) all objects that are not redundant)
    // [ ]  [ ]      create a full JSON from the full list
    // [ ]  [ ] create/save the full JSON to drive (overwrite old)
    // [ ]  [ ] remove all objects from list that do not exist in premade configuration list of names
    // [ ]  [ ] use the new list to create ODF file
    // [ ]  [ ] save ODF file to "EconoStats" folder
    //
    // [ ]  [ ] future: LOG all removals etc.!
    // [ ]  [ ] future: use config file, save in "EconoStats" folder
    // [ ]  [ ] find a good statistics tool and display some nice stats... start with a pie chart!

    public static void main(String args[]) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
        EconoStats econoStats = (EconoStats) context.getBean("econoStats");
        econoStats.start();
    }
}
