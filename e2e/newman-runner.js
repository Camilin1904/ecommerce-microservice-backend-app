const fs = require('fs');
const path = require('path');
const newman = require('newman');
const yargs = require('yargs/yargs');
const { hideBin } = require('yargs/helpers');

const argv = yargs(hideBin(process.argv))
  .option('collection', {
    alias: 'c',
    description: 'Path to Postman collection file',
    type: 'string',
    default: './collections/Ecommerce e2e tests.postman_collection.json'
  })
  .option('environment', {
    alias: 'e',
    description: 'Path to Postman environment file',
    type: 'string'
  })
  .option('data', {
    alias: 'd',
    description: 'Path to data file to use with the collection',
    type: 'string',
    default: './mock-data/mock-data.json'
  })
  .option('report', {
    alias: 'r',
    description: 'Report type (html)',
    type: 'string'
  })
  .help()
  .argv;

// Check if collection file exists
if (!fs.existsSync(argv.collection)) {
  console.error(`Collection file not found: ${argv.collection}`);
  process.exit(1);
}

// Check if data file exists
if (argv.data && !fs.existsSync(argv.data)) {
  console.error(`Data file not found: ${argv.data}`);
  process.exit(1);
}

// Check if environment file exists if provided
if (argv.environment && !fs.existsSync(argv.environment)) {
  console.error(`Environment file not found: ${argv.environment}`);
  process.exit(1);
}

// Create the reporters array based on command line args
const reporters = ['cli'];
if (argv.report === 'html') {
  reporters.push('html');
}

// Newman run configuration
const newmanConfig = {
  collection: require(path.resolve(argv.collection)),
  reporters: reporters,
  reporter: {
    html: {
      export: './reports/report.html'
    }
  }
};

// Add environment if provided
if (argv.environment) {
  newmanConfig.environment = require(path.resolve(argv.environment));
}

// Add iteration data if provided
if (argv.data) {
  const data = require(path.resolve(argv.data));
  newmanConfig.iterationData = data;
}

// Ensure reports directory exists
if (!fs.existsSync('./reports')) {
  fs.mkdirSync('./reports', { recursive: true });
}

// Run Newman
newman.run(newmanConfig)
  .on('start', function () {
    console.log('Newman test run started');
  })
  .on('done', function (err, summary) {
    if (err || summary.error || summary.run.failures.length) {
      console.error('Collection run encountered errors');
      process.exit(1);
    } else {
      console.log('Collection run completed successfully');
    }
  });
