/**
 *  Light-Up Front Door
 *
 *  Copyright © 2016 Phil Maynard
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0												*/
 	       private urlApache() { return "http://www.apache.org/licenses/LICENSE-2.0" }			/*
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *
 *	VERSION HISTORY										*/
 	 private versionNum() {	return "version 2.00" }
     private versionDate() { return "15-Nov-2016" }		/*
 *
 *    v2.00 (15-Nov-2016) - code improvement: store images on GitHub, use getAppImg() to display app images
 *                        - added option to disable icons
 *                        - added option to disable multi-level logging
 *                        - configured default values for app settings
 *						  - moved 'About' to its own page
 *						  - added link to readme file
 *    v1.31 (04-Nov-2016) - update href state & images
 *	  v1.30 (03-Nov-2016) - add option to configure sunset offset
 *    v1.21 (02-Nov-2016) - add link for Apache license
 *                        - code improvement: restrict allowable range for 'leaveOnFor' input
 *    v1.20 (02-Nov-2016) - implement multi-level debug logging function
 *    v1.13 (01-Nov-2016) - code improvement: standardize pages layout
 *	  v1.12 (01-Nov-2016) - code improvement: standardize section headers
 *    v1.11 (26-Oct-2016) - code improvement: added trace for each event handler
 *    v1.10 (26-Oct-2016) - added 'About' section in preferences
 *    v1.00               - initial release, no version tracking up to this point
 *
*/
definition(
    name: "Light-Up Front Door",
    namespace: "astrowings",
    author: "Phil Maynard",
    description: "Turn on a light when someone arrives home and it's dark out.",
    category: "Convenience",
    iconUrl: "http://cdn.device-icons.smartthings.com/Lighting/light9-icn.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Lighting/light9-icn@2x.png",
    iconX3Url: "http://cdn.device-icons.smartthings.com/Lighting/light9-icn@3x.png")


//   ---------------------------
//   ***   APP PREFERENCES   ***

preferences {
	page(name: "pageMain")
    page(name: "pageSettings")
    page(name: "pageLogOptions")
    page(name: "pageAbout")
    page(name: "pageUninstall")
}
    

//   --------------------------------
//   ***   CONSTANTS DEFINITIONS  ***

private		appImgPath()			{ return "https://raw.githubusercontent.com/astrowings/SmartThings/master/images/" }
private		readmeLink()			{ return "https://github.com/astrowings/SmartThings/blob/master/smartapps/astrowings/light-up-front-door.src/readme.md" }


//   -----------------------------
//   ***   PAGES DEFINITIONS   ***

def pageMain() {
    dynamicPage(name: "pageMain", install: true, uninstall: false) {
    	section(){
        	paragraph "", title: "This SmartApp turns on a light when someone arrives home and it's dark out. (e.g. to turn on a porch light)"
        }
        /*
        section("This SmartApp uses luminance as a criteria to trigger actions; select the illuminance-capable " +
                "device to use (if none selected, sunset/sunrise times will be used instead.",
                hideWhenEmpty: true, required: true, state: (theLuminance ? "complete" : null)) {
            //TODO: test using virtual luminance device based on sunrise/sunset
            //TODO: enable use of device everywhere there's a reference to darkness setting (i.e. sunset/sunrise)
            input "theLuminance", "capability.illuminance", title: "Which illuminance device?", multiple: false, required: false, submitOnChange: true
        }
        */
        section("When any of these people arrive") {
            input "people", "capability.presenceSensor", title: "Who?", multiple: true, required: true
        }
        section("Turn on this light") {
            input "theLight", "capability.switch", title: "Which light?", required: true
        }
        section("And leave it on for...") {
            input "leaveOn", "number", title: "Leave on for? (minutes)", description: "max 60 minutes", range: "1..60", defaultValue: 4, required: true
        }
		section() {
            href "pageSettings", title: "App settings", description: "", image: getAppImg("configure_icon.png"), required: false
            href "pageAbout", title: "About", description: "", image: getAppImg("info-icn.png"), required: false
		}
    }
}

def pageSettings() {
	dynamicPage(name: "pageSettings", install: false, uninstall: false) {
        section() {
			label title: "Assign a name", defaultValue: "${app.name}", required: false
            href "pageUninstall", title: "", description: "Uninstall this SmartApp", image: getAppImg("trash-circle-red-512.png"), state: null, required: true
		}
        //TODO: option to override darkness (i.e. run even during daytime)
        if (!theLuminance) {
            section("This SmartApp uses the sunset/sunrise time to evaluate luminance as a criteria to trigger actions. " +
                    "If required, you can adjust the amount time before/after sunset when the app considers that it's dark outside " +
                    "(e.g. use '-20' to adjust the sunset time 20 minutes earlier than actual).") {
                input "sunsetOffset", "number", title: "Sunset offset time", description: "How many minutes (+/- 60)?", range: "-60..60", required: false
            }
   		}
        section("Debugging Options", hideable: true, hidden: true) {
            input "noAppIcons", "bool", title: "Disable App Icons", description: "Do not display icons in the configuration pages", image: getAppImg("disable_icon.png"), defaultValue: false, required: false, submitOnChange: true
            href "pageLogOptions", title: "IDE Logging Options", description: "Adjust how logs are displayed in the SmartThings IDE", image: getAppImg("office8-icn.png"), required: true, state: "complete"
        }
    }
}

def pageAbout() {
	dynamicPage(name: "pageAbout", title: "About this SmartApp", install: false, uninstall: false) { //with 'install: false', clicking 'Done' goes back to previous page
		section() {
        	href url: readmeLink(), title: app.name, description: "Copyright ©2016 Phil Maynard\n${versionNum()}", image: getAppImg("readme-icn.png")
            href url: urlApache(), title: "License", description: "View Apache license", image: getAppImg("license-icn.png")
		}
    }
}

def pageLogOptions() {
	dynamicPage(name: "pageLogOptions", title: "IDE Logging Options", install: false, uninstall: false) {
        section() {
	        input "debugging", "bool", title: "Enable debugging", description: "Display the logs in the IDE", defaultValue: false, required: false, submitOnChange: true 
        }
        if (debugging) {
            section("Select log types to display") {
                input "log#info", "bool", title: "Log info messages", defaultValue: true, required: false 
                input "log#trace", "bool", title: "Log trace messages", defaultValue: true, required: false 
                input "log#debug", "bool", title: "Log debug messages", defaultValue: true, required: false 
                input "log#warn", "bool", title: "Log warning messages", defaultValue: true, required: false 
                input "log#error", "bool", title: "Log error messages", defaultValue: true, required: false 
			}
            section() {
                input "setMultiLevelLog", "bool", title: "Enable Multi-level Logging", defaultValue: true, required: false,
                    description: "Multi-level logging prefixes log entries with special characters to visually " +
                        "represent the hierarchy of events and facilitate the interpretation of logs in the IDE"
            }
        }
    }
}

def pageUninstall() {
	dynamicPage(name: "pageUninstall", title: "Uninstall", install: false, uninstall: true) {
		section() {
        	paragraph "CAUTION: You are about to completely remove the SmartApp '${app.name}'. This action is irreversible. If you want to proceed, tap on the 'Remove' button below.",
                required: true, state: null
        }
	}
}


//   ----------------------------
//   ***   APP INSTALLATION   ***

def installed() {
	debug "installed with settings: ${settings}", "trace"
    initialize()
}

def updated() {
    debug "updated with settings ${settings}", "trace"
	unsubscribe()
    unschedule()
    initialize()
}

def uninstalled() {
    state.debugLevel = 0
    debug "application uninstalled", "trace"
}

def initialize() {
    state.debugLevel = 0
    debug "initializing", "trace", 1
    subscribeToEvents()
    debug "initialization complete", "trace", -1
}

def subscribeToEvents() {
    debug "subscribing to events", "trace", 1
	subscribe(people, "presence.present", presenceHandler)
    subscribe(location, "position", locationPositionChange) //update settings if hub location changes
    //TODO: subscribe to lights on/off events IF commanded by this app (and log events)
    debug "subscriptions complete", "trace", -1
}


//   --------------------------
//   ***   EVENT HANDLERS   ***

def presenceHandler(evt) {
    debug "presenceHandler event: ${evt.descriptionText}", "trace", 1
    debug "$evt.displayName has arrived", "info"

	if (itsDarkOut && (theLight.currentSwitch != "on")) {
        debug "call to turn on the $theLight.displayName"
        turnOn()
    } else {
        debug "conditions not met; do nothing"
    }
    debug "presenceHandler complete", "trace", -1
}

def locationPositionChange(evt) {
    debug "locationPositionChange(${evt.descriptionText})", "warn"
	initialize()
}


//   -------------------
//   ***   METHODS   ***

def turnOn() {
    debug "executing turnOn()", "trace", 1
    theLight.on()
    debug "scheduling $theLight.displayName to turn off in $leaveOn minutes", "info"
    runIn(60 * leaveOn, turnOff)
    debug "turnOn() complete", "trace", -1
}

def turnOff() {
    debug "executing turnOff()", "trace", 1
    theLight.off()
    debug "turnOff() complete", "trace", -1
}


//   -------------------------
//   ***   APP FUNCTIONS   ***



//   ------------------------
//   ***   COMMON UTILS   ***

def getItsDarkOut() { //implement use of illuminance capability
    def sunTime = getSunriseAndSunset(sunsetOffset: sunsetOffset)
    def nowDate = new Date(now() + 2000) // be safe and set current time for 2 minutes later
    def result = false
    def desc = ""
	
    if(sunTime.sunrise < nowDate && sunTime.sunset > nowDate){
    	desc = "it's daytime"
        result = false
    } else {
    	desc = "it's nighttime"
        result = true
    }
    debug ">> itsDarkOut : $result ($desc)"
    return result
}

def getAppImg(imgName, forceIcon = null) {
	def imgPath = appImgPath()
    return (!noAppIcons || forceIcon) ? "$imgPath/$imgName" : ""
}

def debug(message, lvl = null, shift = null, err = null) {
	
    def debugging = settings.debugging
	if (!debugging) {
		return
	}
    
    lvl = lvl ?: "debug"
	if (!settings["log#$lvl"]) {
		return
	}
	
    def multiEnable = (settings.setMultiLevelLog == false ? false : true) //set to true by default
    def maxLevel = 4
	def level = state.debugLevel ?: 0
	def levelDelta = 0
	def prefix = "║"
	def pad = "░"
	
    //shift is:
	//	 0 - initialize level, level set to 1
	//	 1 - start of routine, level up
	//	-1 - end of routine, level down
	//	 anything else - nothing happens
	
    switch (shift) {
		case 0:
			level = 0
			prefix = ""
			break
		case 1:
			level += 1
			prefix = "╚"
			pad = "═"
			break
		case -1:
			levelDelta = -(level > 0 ? 1 : 0)
			pad = "═"
			prefix = "╔"
			break
	}

	if (level > 0) {
		prefix = prefix.padLeft(level, "║").padRight(maxLevel, pad)
	}

	level += levelDelta
	state.debugLevel = level

	if (multiEnable) {
		prefix += " "
	} else {
		prefix = ""
	}

    if (lvl == "info") {
    	def leftPad = (multiEnable ? ": :" : "")
        log.info "$leftPad$prefix$message", err
	} else if (lvl == "trace") {
    	def leftPad = (multiEnable ? "::" : "")
        log.trace "$leftPad$prefix$message", err
	} else if (lvl == "warn") {
    	def leftPad = (multiEnable ? "::" : "")
		log.warn "$leftPad$prefix$message", err
	} else if (lvl == "error") {
    	def leftPad = (multiEnable ? "::" : "")
		log.error "$leftPad$prefix$message", err
	} else {
		log.debug "$prefix$message", err
	}
}