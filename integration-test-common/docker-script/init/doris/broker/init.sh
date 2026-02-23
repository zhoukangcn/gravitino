#!/bin/bash

# Configure Broker
mkdir -p /opt/apache-doris/apache_hdfs_broker/conf
cat > /opt/apache-doris/apache_hdfs_broker/conf/apache_hdfs_broker.conf << EOF
broker_ipc_port = 8000
broker_http_port = 8001
EOF

# Wait for FE to be available
while true; do
  if curl -f http://fe:8030/api/bootstrap; then
    echo "FE is available"
    break
  fi
  echo "Waiting for FE to be available..."
  sleep 5
done

# Start Broker
/opt/apache-doris/apache_hdfs_broker/bin/start_broker.sh --daemon

# Wait for Broker to start
while true; do
  if curl -f http://fe:8030/api/health?type=broker; then
    echo "Broker started successfully"
    break
  fi
  echo "Waiting for Broker to start..."
  sleep 5
done
