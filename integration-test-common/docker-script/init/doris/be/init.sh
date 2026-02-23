#!/bin/bash

# Configure BE
mkdir -p /opt/apache-doris/be/conf
cat > /opt/apache-doris/be/conf/be.conf << EOF
be_port = 9060
be_http_port = 8040
be_heartbeat_service_port = 9050
be_storage_root_path = /opt/apache-doris/be/storage
EOF

# Create storage directory
mkdir -p /opt/apache-doris/be/storage

# Wait for FE to be available
while true; do
  if curl -f http://fe:8030/api/bootstrap; then
    echo "FE is available"
    break
  fi
  echo "Waiting for FE to be available..."
  sleep 5
done

# Register BE with FE
/opt/apache-doris/be/bin/start_be.sh --daemon --helper fe:9010

# Wait for BE to start
while true; do
  if curl -f http://fe:8030/api/health?type=be; then
    echo "BE started successfully"
    break
  fi
  echo "Waiting for BE to start..."
  sleep 5
done
