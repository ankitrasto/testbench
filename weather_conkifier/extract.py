#!/usr/bin/python

import os
import sys
import argparse
import urllib2
import re
import datetime

#fltCurrentTemp = None
#fltHighTemp = None
#fltLowTemp = None
#fltWindSpeed = None
#fltPressure = None
#strWindDir = ''
#strCurrentConditions = ''
#strXMLURL = ''
#strType = ''
#DEFAULT_XMLURL = "http://www.google.ca"



def environmentCanada(data): #environment Canada RSS
	strMasterForecast = ""
	
	#extract CDATA - current conditions
	strCDATA = (substring("![CDATA[", "]]>", data).replace("<b>", "")).replace("</b>", "").replace("<br/>", "")
	#print strCDATA
	
	fltCurrentTemp = float(re.findall("[-+]?\d*\.\d+|\d+", substring("Temperature:", "\n", strCDATA))[0])
	#print fltCurrentTemp
	strMasterForecast += str(fltCurrentTemp) + "\n"
	
	windDir = re.findall("[A-Z]+", substring("Wind:", "\n", strCDATA))[0]
	#print windDir
	
	fltWindSpeed = float(re.findall("\d+", substring("Wind:", "\n", strCDATA))[0])
	#print fltWindSpeed
	strMasterForecast += str(fltWindSpeed) + " km/h " + windDir +"\n"
	
	strCurrentConditions = (substring("Condition: ", "\n", strCDATA).split("\n")[0]).strip()
	#print strCurrentConditions
	strMasterForecast += strCurrentConditions + "\n"
	
	strPressureTendency = (substring("Tendency: ", "\n", strCDATA).split("\n")[0])
	#print strPressureTendency
	strMasterForecast += strPressureTendency + "\n"
	
	#next 2 days, forecast, first get current day from original forecast data:
	today = datetime.datetime.today()
		
	#next n days:	
	for i in range (1,3):
		highString = (today+datetime.timedelta(days=i)).strftime("%A")+": " #next day in full text
		strFuture = (substring(highString,None,data).split("\n")[0])
		strFutureArray = strFuture.split(" ")
		
		fltFutureHigh = float(re.findall("\d+", substring("High",None,strFuture))[0])
		strFutureCond = strFuture.split(".")[0]
		if(strFuture.find("minus") >= 0): fltFutureHigh *= -1
		#print strFutureCond
		#print fltFutureHigh, "H"
		strMasterForecast += strFutureCond + "\n" + str(int(round(fltFutureHigh))) + " H\n"
		
		
		highString = (today+datetime.timedelta(days=i)).strftime("%A")+" night:"
		strFuture = (substring(highString,None,data).split("\n")[0])
		fltFutureLow = float(re.findall("\d+", substring("Low",None,strFuture))[0])
		if(strFuture.find("minus") >= 0): fltFutureLow *= -1
		#print fltFutureLow, "L"
		strMasterForecast += str(int(round(fltFutureLow))) + " L\n"
		
	return strMasterForecast
	
def wu(data): #weather underground RSS
	strMasterForecast = ""
	
	strMasterForecast += getXMLfield("full", getXMLfield("display_location", data))
	strMasterForecast += getXMLfield("temp_c", data) + "\n"
	#finish here
	
	return strMasterForecast

def getXML(auxURL):
	auxFile = urllib2.urlopen(auxURL)
	return auxFile.read(50000)
	
def getXMLfield(fieldName, data):
	 startField = "<" + fieldName + ">" 
	 endField = "</" + fieldName + ">"
	 return substring(startField, endField, data)
	
#relative string extraction
def substring(beginKey, endKey, auxData):
	if endKey == None:
		return auxData[auxData.index(beginKey)+len(beginKey):]
	else:
		return auxData[auxData.index(beginKey)+len(beginKey):auxData.rindex(endKey)]
	

def processArgs():
	parser = argparse.ArgumentParser(description="extract weather data from XML/RSS feeds")
	parser.add_argument("-u", "--url", help="source URL")
	parser.add_argument("-t", "--sourcetype", help="weather type (Environment Canada, Weather Underground)", choices=["EnvCan","WU"])
	
	args = parser.parse_args()
	strType = args.sourcetype
	strXMLURL = args.url
	
	if(strXMLURL == None or strType == None):
		print "must specify an XML url: see program help (-h) for details"
		sys.exit()
	
	strDataHold = (getXML(strXMLURL))
	strFinalForecast = ""
	if(strType == "EnvCan"): 
		strFinalForecast = environmentCanada(strDataHold)
	
	if(strType == "WU"):
		strFinalForecast = wu(strDataHold)
	
	#print strFinalForecast
	tempWriter = open("parsed_forecast.txt", "w")
	tempWriter.write(strFinalForecast)
	tempWriter.close()
	
def main():
	processArgs()

if __name__ == "__main__":
    main()
