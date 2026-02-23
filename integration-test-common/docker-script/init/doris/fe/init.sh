#!/bin/bash

# Configure FE
mkdir -p /opt/apache-doris/fe/conf
cat > /opt/apache-doris/fe/conf/fe.conf << EOF
http_port = 8030
rpc_port = 9020
query_port = 9030
edit_log_port = 9010
EOF

# Start FE
/opt/apache-doris/fe/bin/start_fe.sh --daemon

# Wait for FE to start
while true; do
  if curl -f http://localhost:8030/api/bootstrap; then
    echo "FE started successfully"
    break
  fi
  echo "Waiting for FE to start..."
  sleep 5
done
