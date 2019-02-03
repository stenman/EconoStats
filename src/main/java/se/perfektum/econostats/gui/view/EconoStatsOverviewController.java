package se.perfektum.econostats.gui.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.perfektum.econostats.gui.EconoStatsMain;

public class EconoStatsOverviewController {
    final Logger LOGGER = LoggerFactory.getLogger(EconoStatsOverviewController.class);

    // Reference to the main application.
    private EconoStatsMain econoStatsMain;

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param econoStatsMain
     */
    public void setEconoStatsMain(EconoStatsMain econoStatsMain) {
        LOGGER.debug("Setting main reference");
        this.econoStatsMain = econoStatsMain;
    }

}
