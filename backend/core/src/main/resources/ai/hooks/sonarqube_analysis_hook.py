#ACR-fc367308ebdb4870a9c49f1833198afc
#ACR-8fe024b6a02f4e21971a779868058f78
#ACR-1459b6e6538b4ba89f58bb4236596eae
#ACR-993f109ccfa645b7897fb215e0f9c11b

import sys
import json
import urllib.request
import urllib.error

STARTING_PORT = 64120
ENDING_PORT = 64130
EXPECTED_IDE_NAME = '{{AGENT}}'
PORT_SCAN_TIMEOUT = 0.1

def find_backend_port():
    """ACR-d4f0834cb06b46b5964a0e5d5fd9221d"""
    for port in range(STARTING_PORT, ENDING_PORT + 1):
        if check_port(port):
            return port
    return None

def check_port(port):
    """ACR-1c7dd484deb14edc841b8a1f60221b3b"""
    try:
        url = f'http://localhost:{port}/sonarlint/api/status'
        req = urllib.request.Request(url, headers={'Origin': 'ai-agent://{{AGENT}}'})
        with urllib.request.urlopen(req, timeout=PORT_SCAN_TIMEOUT) as response:
            if response.status == 200:
                data = json.loads(response.read().decode('utf-8'))
                ide_name = data.get('ideName')
                if ide_name == EXPECTED_IDE_NAME:
                    return True
    except Exception:
        pass
    return False

def analyze_file(port, file_path):
    """ACR-c9ce25de808845239ff95e1898e1320b"""
    print(f'Analyzing: {file_path} (port {port})')
    request_body = json.dumps({'fileAbsolutePaths': [file_path]})
    url = f'http://localhost:{port}/sonarlint/api/analysis/files'
    req = urllib.request.Request(
        url,
        data=request_body.encode('utf-8'),
        headers={
            'Content-Type': 'application/json',
            'Origin': 'ai-agent://{{AGENT}}'
        }
    )

    try:
        response = urllib.request.urlopen(req, timeout=1)
        response.close()
    except Exception as e:
        print(f'Error: {e}')

    sys.exit(0)

def main():
    try:
        event_json = sys.stdin.read()
        event = json.loads(event_json)

        tool_info = event.get('tool_info', {})
        file_path = tool_info.get('file_path')
        
        if not file_path:
            print('No file path in event')
            return

        port = find_backend_port()
        if not port:
            print('Backend not found')
            sys.exit(1)

        analyze_file(port, file_path)
    
    except json.JSONDecodeError as e:
        print(f'JSON error: {e}')
        sys.exit(1)
    except Exception as e:
        print(f'Error: {e}')
        sys.exit(1)

if __name__ == '__main__':
    main()

