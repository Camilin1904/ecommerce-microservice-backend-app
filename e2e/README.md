# E2E Testing with Newman

This directory contains the setup for running E2E tests using Newman, the command-line collection runner for Postman.

## Setup

1. Install dependencies:
   ```
   npm install
   ```

2. Add your Postman collection JSON to the `collections` folder
3. Add your mock data JSON to the `mock-data` folder

## Running Tests

Run the tests with default options:
```
npm test
```

Run the tests with an HTML report:
```
npm run test:html-report
```

Run the tests with custom options:
```
node newman-runner.js --collection ./collections/your-collection.json --data ./mock-data/your-data.json
```

## Command Line Options

- `--collection, -c`: Path to Postman collection JSON file (default: './collections/collection.json')
- `--environment, -e`: Path to Postman environment JSON file (optional)
- `--data, -d`: Path to data file to use with the collection (default: './mock-data/mock-data.json')
- `--report, -r`: Report type, e.g., 'html' (optional)

## Directory Structure

- `collections/`: Store your Postman collection JSON files here
- `mock-data/`: Store your mock data JSON files here
- `reports/`: Generated test reports will be saved here
