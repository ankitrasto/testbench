#weather conkifier

A python script used for extracting weather information from XML files of certain weather services. I was motivated to make this as a fix for my conky weather scripts, which stopped working after Yahoo weather updated their API to use authentication.

##Usage/Input:
run it as a bash command:

    ./extract.py -u <URL of XML file> -t <Service>

currently the options are:
    <Service> = "EnvCan": environment canada (specify the forecast XML for this)
    <Service> = "WU": weather underground (work in progress) - the current weather can be extracted but the forecast requires an API key

##Output:

The program outputs (and/or overwrites) a text file "parsed_forecast.txt", in the format (let n be the current day):
```<Current Temperature in deg.C or F>

<Wind Speed + Direction>

<Pressure + Tendency>

<Current weather condition, text>

<Next Day's Weather condition, text>

<Next Day's forecasted high>

<Next Day's forecasted low>

<day n+2: weather condition, text>

<day n+2: forecasted high>

<day n+3: forecasted low>

EOF.```


See parsed_forecast.txt above for an example. The day n+x forecast can be iteratively be repeated for x=1-7, if the XML source file contains such information.

