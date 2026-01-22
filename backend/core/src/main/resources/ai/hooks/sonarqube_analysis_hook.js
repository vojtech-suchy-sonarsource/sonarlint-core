#!/usr/bin/env node
//ACR-eec36d5e7d874d9b81437868c9c4ef4e
//ACR-aff1490ffe5e41619637d7ee33473ea3
//ACR-33a0b9d8a7de49af8724b33c615ae8a0

const http = require('node:http');

const STARTING_PORT = 64120;
const ENDING_PORT = 64130;
const EXPECTED_IDE_NAME = '{{AGENT}}';
const PORT_SCAN_TIMEOUT = 100;

async function findBackendPort() {
  const portPromises = [];
  for (let port = STARTING_PORT; port <= ENDING_PORT; port++) {
    portPromises.push(checkPort(port));
  }
  const results = await Promise.allSettled(portPromises);
  for (const result of results) {
    if (result.status === 'fulfilled' && result.value !== null) {
      return result.value;
    }
  }
  return null;
}

function checkPort(port) {
  return new Promise((resolve) => {
    const req = http.get({
      hostname: 'localhost',
      port,
      path: '/sonarlint/api/status',
      timeout: PORT_SCAN_TIMEOUT,
      headers: {
        'Origin': 'ai-agent://{{AGENT}}'
      }
    }, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => {
        try {
          const status = JSON.parse(data);
          if (status.ideName === EXPECTED_IDE_NAME) {
            resolve(port);
          } else {
            resolve(null);
          }
        } catch (e) {
          resolve(null);
        }
      });
    });
    
    req.on('error', () => {
      resolve(null);
    });
    req.on('timeout', () => {
      req.destroy();
      resolve(null);
    });
  });
}

function analyzeFile(port, filePath) {
  console.log(`Analyzing: ${filePath} (port ${port})`);
  const requestBody = JSON.stringify({ fileAbsolutePaths: [filePath] });
  const options = {
    hostname: 'localhost',
    port,
    path: '/sonarlint/api/analysis/files',
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Content-Length': Buffer.byteLength(requestBody),
      'Origin': 'ai-agent://{{AGENT}}'
    },
    agent: new http.Agent({ keepAlive: false }),
    timeout: 1000
  };
  const req = http.request(options);
  req.on('socket', (socket) => {
    socket.unref();
  });
  req.on('error', (err) => {
    console.log(`Error: ${err.message}`);
  });
  req.write(requestBody);
  req.end();
  setImmediate(() => {
    process.exit(0);
  });
}

//ACR-05c0afad57754aae9c4cc2053314661e
let eventJson = '';
process.stdin.setEncoding('utf8');

process.stdin.on('data', (chunk) => {
  eventJson += chunk;
});

process.stdin.on('end', async () => {
  try {
    const event = JSON.parse(eventJson);
    const filePath = event.tool_info?.file_path;
    if (!filePath) {
      console.log('No file path in event');
      return;
    }
    const port = await findBackendPort();
    if (!port) {
      console.log('Backend not found');
      process.exit(1);
    }
    analyzeFile(port, filePath);
  } catch (e) {
    console.log(`Error: ${e.message}`);
    process.exit(1);
  }
});

