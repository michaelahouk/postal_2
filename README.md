# Postal Part 2
This application unzips and parses a .csv file containing information about HTTP calls to a SEC website and aggregates data from this file.

# To run:
1. Clone the repo locally
2. Navigate to the project directory
3. Download the .zip file at http://www.sec.gov/dera/data/Public-EDGAR-log-file-data/2017/Qtr2/log20170630.zip
4. Run command `docker build -t postal2 .` to build the image
5. Run command `docker run postal2:latest` to run the image
6. To view the results of the data parsing, navigate to the `\results` directory and view: 
    1. ip.txt - contains unique IP addresses from calls
    2. codes.txt - contains all status codes and their counts 
    3. size.txt - the total size in bytes of all data 
    