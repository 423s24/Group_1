package HRDC_Warming_Hut_Database_Management;

import net.java.html.boot.BrowserBuilder;

class DesktopMain {

    public static void main(String[] args) {
        BrowserBuilder.newBrowser().loadPage("pages/index.html")
                .loadFinished(Demo::onPageLoad)
                .showAndWait();
        System.exit(0);
    }
}
