/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webbrowser;

import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 *
 * @author Eric Canull
 */
public class BrowserLoader {
    
    private Supplier<WebController> browser = () -> createAndCacheBrowser();

    private static final Logger LOGGER = Logger.getLogger(WebController.class.getName());
    
    public BrowserLoader() {
            LOGGER.info("Browser created");
    }

    public WebController getBrowser() {
        return browser.get();
    }

    private synchronized WebController createAndCacheBrowser() {
        
        class WebBrowserFactor implements Supplier<WebController> {

            private final WebController browserInstance = new WebController();

            @Override
            public WebController get() {
                return browserInstance;
            }
        }

        if (!WebBrowserFactor.class.isInstance(browser)) {
            browser = new WebBrowserFactor();
        }
        return browser.get();
    }
}
