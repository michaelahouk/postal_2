# Postal Part 2
This application downloads, unzips, and parses a .csv file containing information about HTTP calls to a SEC website and aggregates data from this file.

# To run:
1. Clone the repo locally
2. Navigate to the project directory
3. Run command `docker build -t postal2 .` to build the image
4. Run command `docker run postal2:latest > results.txt` to run the image
5. View the results stored in the results.txt file that was generated from the previous comman
6. (Optional) remove the results file by running `./cleanup.sh`
    