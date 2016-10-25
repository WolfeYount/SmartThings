/**
 *  Test
 *
 *  Copyright 2016 Phil Maynard
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Developer retains all right, title, copyright, and interest, including all copyright, patent rights, trade secret 
 *  in the Background technology. May be subject to consulting fees under the Agreement between the Developer and the Customer. 
 *  Developer grants a non exclusive perpetual license to use the Background technology in the Software developed for and delivered 
 *  to Customer under this Agreement. However, the Customer shall make no commercial use of the Background technology without
 *  Developer's written consent.
 */
definition(
    name: "Test - Time",
    namespace: "astrowings",
    author: "Phil Maynard",
    description: "Test",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
	section("When this switch is turned on"){
		input "theswitch", "capability.switch"
	}
    section("Time input"){
    	input "timeString", "time", required: false
    }
}

def installed() {
	initialize()
}

def updated() {
    unsubscribe()
    initialize()
}

def initialize() {
	subscribe(theswitch, "switch", runTest)
	runTest()
}

def runTest(evt) {
//	log.info " *** EVENT PROPERTIES ***"
//	log.debug "The runTest event was triggered on :: $evt.date (Date)"
//	log.debug "The runTest event dateValue is :: $evt.dateValue (Date)"
//	log.debug "The runTest event isoDate is :: $evt.isoDate (String)"

//	state.nowDate = new Date()
	testSun()
//  testDate()
//	testString()
//  testParsing()
//	testJava()
//	listTimeZones()
//	testTimeZone()
//  testUnix()
//	testState()
//	testTimeInput()
}

def testSun() {
	log.info " *** THE SUN ***"
    def sunTime = getSunriseAndSunset()
    log.debug "the sunrise date from getSunriseAndSunset (today's sunrise) is :: $sunTime.sunrise (Date)"
    log.debug "the sunrise string for this location (next sunrise) is :: ${location.currentValue("sunriseTime")} (String - the evt.value for a location's sunsetTime/sunriseTime event is also a String)"
    log.debug "the sunset date from getSunriseAndSunset (today's sunset) is :: $sunTime.sunset (Date)"
    log.debug "the sunset string for this location (next sunset) is :: ${location.currentValue("sunsetTime")} (String - the evt.value for a location's sunsetTime/sunriseTime event is also a String)"
}

def testDate() {
	log.info " *** DATE FORMAT ***"
    log.debug "new Date() :: ${new Date()}"
    def nowDate = state.nowDate
    log.debug "nowDate.format(\"E MMM dd HH:mm:ss z yyyy\") :: ${nowDate.format("E MMM dd HH:mm:ss z yyyy")}"
    log.debug "nowDate.format(\"EEEE hhmm a\") :: ${nowDate.format("EEEE hhmm a")}"
}

def testString() {
    log.info " *** STRING FORMAT ***"
    def nowDate = new Date()
    log.debug "convert Date to String - nowDate.toString() :: ${nowDate.toString()}"
    def nowString = nowDate.toString()
    log.debug "modify String - nowString.replace(\"UTC\", \"Zulu\").toUpperCase() :: ${nowString.replace("UTC", "Zulu").toUpperCase()}"
}

def testParsing() {
    log.info " *** PARSING A STRING INTO A DATE ***"
    log.debug "parse the string \"2016-08-21T04:46:35.018Z\" :: ${Date.parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "2016-08-21T04:46:35.018Z")}"
    def myDate = new Date().parse("yyyy/MM/dd", "2016/08/20")
    state.myDate = myDate
    log.debug "new Date().parse(\"yyyy/MM/dd\", \"2016/08/20\") :: $myDate"
}

def testUnix() {
    log.info " *** UNIX DATE FORMAT ***"
    def myDate = state.myDate
    log.debug "convert Date to Unix - myDate.time :: $myDate.time"
    def myUnix = myDate.time
    log.debug "convert Unix to Date - new Date(myUnix) :: ${new Date(myUnix)}"
    log.debug "apply offset (+/- ms) to Unix time - new Date(myUnix + 30 * 60 * 1000) :: ${new Date(myUnix + 30 * 60 * 1000)}"
}

def testJava() {
	//method to obtain current local time
	def datNow = new Date()
    log.debug "datNow :: $datNow"
    def nowDOW = datNow.format("EEEE")
    log.debug "nowDOW :: $nowDOW"
    
    def javaDate = new java.text.SimpleDateFormat("EEEE, dd MMM yyyy @ HH:mm:ss")
    log.debug "javaDate :: $javaDate"
    if (location.timeZone) {
    	log.debug "location.timeZone = true"
        javaDate.setTimeZone(location.timeZone)
    } else {
        log.debug "location.timeZone = false"
        //do nothing, just use UTC
        //javaDate.setTimeZone(TimeZone.getTimeZone("America/Edmonton"))
    }
    def strDate = javaDate.format(datNow)
    log.debug "strDate :: $strDate"
}

def listTimeZones() {
    def availId = TimeZone.getAvailableIDs()
	log.debug "Available IDs ($availId.length) are: (uncomment to list)"
//	for (int i=0; i<availId.length; i++) {
//		log.debug availId[i]
//	} 
}

def testTimeZone() {
	def datNow = new Date()
    log.debug "datNow :: $datNow"
    def strNow = datNow.format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    log.debug "strNow :: $strNow"
	log.debug "timeToday(strNow, location.timeZone) :: ${timeToday(strNow, location.timeZone)}"
}

def testState() {
	log.info " *** STORING DATE IN APP STATE ***"
    def myDate = new Date()
    log.debug "myDate (Date format): $myDate"
	state.testStateDate = myDate
    log.debug "state.testStateDate: $state.testStateDate"
    retrieveState()
}

def retrieveState() {
	log.info " *** RETRIEVING DATE IN APP STATE ***"
	log.debug "state.testStateDate: $state.testStateDate"
    def myDate = state.testStateDate
    log.debug "myDate (from State): $myDate"
}

def testTimeInput() {
	def tz = location.timeZone
    log.debug "timeString :: $timeString"
    log.debug "timeToday(timeString, tz) :: ${timeToday(timeString, tz)}"
    log.debug "timeToday(timeString, tz).time :: ${timeToday(timeString, tz).time}"
    log.debug "timeToday(timeString, tz).time + 60000 :: ${timeToday(timeString, tz).time + 60000}"
    log.debug "new Date(timeToday(timeString, tz).time + 60000) :: ${new Date(timeToday(timeString, tz).time + 60000)}"
}